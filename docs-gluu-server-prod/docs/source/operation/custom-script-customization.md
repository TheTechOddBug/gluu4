# Overview

The following docs is a guide on how to switch from Jackrabbit (JCA) to custom scripts to apply customization.

## oxAuth customization

1.  Steps to apply oxAuth customization:

    1.  Get all directories and files used for customization.

        Example, given the following directory:

        ```
        /path/to/oxauth-customization
        ├── i18n
        ├── libs
        ├── pages
        │   └── example.xhtml
        └── static
        ```

        Create archive file using `tar`:

        ```sh
        cd /path/to/oxauth-customization
        tar cvzf oxauth-custom.tar.gz *
        ```

    1.  Upload the `oxauth-custom.tar.gz` somewhere so we can download the file.

    1.  Create custom script `oxauth-custom.sh` to download and extract the `oxauth-custom.tar.gz`:

        ```sh
        #!/bin/sh
        # step 1: download oxauth-custom.tar.gz
        wget https://$SERVER/oxauth-custom.tar.gz -O /tmp/oxauth-custom.tar.gz
        # step 2: extract oxauth-custom.tar.gz
        tar xvf /tmp/oxauth-custom.tar.gz -C /opt/gluu/jetty/oxauth/custom
        ```

    1.  Create configmap to store the `oxauth-custom.sh` file:

        ```sh
        kubectl -n $NAMESPACE create cm oxauth-custom --from-file=oxauth-custom.sh
        ```

    1.  Modify `values.yaml` to add new volumes and custom script:

        ```yaml
        oxauth:
          customScripts:
            - /tmp/oxauth-custom.sh
          volumes:
            - name: oxauth-custom
              configMap:
                name: oxauth-custom
                defaultMode: 493
          volumeMounts:
            - name: oxauth-custom
              mountPath: /tmp/oxauth-custom.sh
              subPath: oxauth-custom.sh
        ```

    1. run `helm install` or `helm upgrade`
    
## oxTrust customization

1.  Steps to apply oxTrust customization:

    1.  Get all directories and files used for customization.

        Example, given the following directory:

        ```
        /path/to/oxtrust-customization
        ├── i18n
        ├── libs
        ├── pages
        │   └── example.xhtml
        └── static
        ```

        Create archive file using `tar`:

        ```sh
        cd /path/to/oxtrust-customization
        tar cvzf oxtrust-custom.tar.gz *
        ```

    1.  Upload the `oxtrust-custom.tar.gz` somewhere so we can download the file.

    1.  Create custom script `oxtrust-custom.sh` to download and extract the `oxtrust-custom.tar.gz`:

        ```sh
        #!/bin/sh
        # step 1: download oxtrust-custom.tar.gz
        wget https://$SERVER/oxtrust-custom.tar.gz -O /tmp/oxtrust-custom.tar.gz
        # step 2: extract oxtrust-custom.tar.gz
        tar xvf /tmp/oxtrust-custom.tar.gz -C /opt/gluu/jetty/identity/custom
        ```

    1.  Create configmap to store the `oxtrust-custom.sh` file:

        ```sh
        kubectl -n $NAMESPACE create cm oxtrust-custom --from-file=oxtrust-custom.sh
        ```

    1.  Modify `values.yaml` to add new volumes and custom script:

        ```yaml
        oxtrust:
          customScripts:
            - /tmp/oxtrust-custom.sh
          volumes:
            - name: oxtrust-custom
              configMap:
                name: oxtrust-custom
                defaultMode: 493
          volumeMounts:
            - name: oxtrust-custom
              mountPath: /tmp/oxtrust-custom.sh
              subPath: oxtrust-custom.sh
        ```

    1. run `helm install` or `helm upgrade`

