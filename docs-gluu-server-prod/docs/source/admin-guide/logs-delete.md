---
tags:
  - administration
  - reference
  - kubernetes
  - logs
  - delete
---

## Overview
This document demostrates how to create a `CronJob` in CN that deletes log files older than 15 days.
This showcases deleting old log files in `oxauth`.
You can adjust it as needed.


```yaml
apiVersion: batch/v1
kind: CronJob
metadata:
  name: log-cleanup-cronjob
  namespace: gluu # We assume the namespace is "gluu". Adjust as needed in the whole cronJob as needed.
spec:
  schedule: "0 0 * * *"  # Run this at midnight daily (adjust as needed)
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: log-cleanup
            image: bitnami/kubectl:latest  # Using the kubectl image
            command:
              - /bin/sh
              - -c
              - |
                # Get the latest oxauth pod name dynamically using kubectl
                POD_NAME=$(kubectl get pod -l app=oxauth -n gluu -o jsonpath='{.items[0].metadata.name}')
                
                # Run cleanup on the latest oxauth pod
                kubectl exec -it $POD_NAME -n gluu -- find /opt/gluu/jetty/oxauth/logs/ -type f -name "*.log" -mtime +15 -delete
            env:
              - name: KUBERNETES_SERVICE_HOST
                value: "kubernetes.default.svc"
              - name: KUBERNETES_SERVICE_PORT
                value: "443"
          restartPolicy: OnFailure
```