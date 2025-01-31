## Building Gluu Server

### Below are the steps to build packages
- Create ephemeral instance from easycloud.
- Transfer files via scp from `central.gluu.info` to your instance.
- For step 2, you need to add `central.gluu.info`'s root user's public key to your instance created from easycloud.
- Files are placed at: ls /backup/packages/ at `central.gluu.info`. 
- The files used for packages for all distros are:
  - CE packages for chroot containers are as below:
```
root@central:/backup/packages# ls -l *CE*
-rw-r--r-- 1 backup backup  823440442 Dec 10 16:58 focal-Gluu-CE-Chroot.tgz
-rw-r--r-- 1 backup backup  515203001 Nov 30 08:24 jammy-Gluu-CE-Chroot.tgz
-rw-r--r-- 1 backup backup 1124604412 Dec  8 07:31 rhel8-Gluu-CE-Chroot.tgz
-rw-r--r-- 1 backup backup  599003621 Dec  8 07:14 rhel9-Gluu-CE-Chroot.tgz
```
  - The nochoort packaging files are as below:
```
root@central:/backup/packages# ls -l  *nochroot*
-rw-r--r-- 1 backup backup   59009 Jan 28 17:06 rhel-gluu-server-nochroot.tgz
-rw-r--r-- 1 backup backup 5625398 Jan 28 19:16 suse-gluu-server-nochroot.tgz
-rw-r--r-- 1 backup backup    4966 Dec  6 18:00 ubuntu-gluu-server-nochroot.tgz
root@central:/backup/packages# 
```
  - The packaging tools directory is as below:
```
root@central:/backup/packages# ls -l packaging-tools.tgz 
-rw-r--r-- 1 backup backup 35911 Dec 10 17:07 packaging-tools.tgz
root@central:/backup/packages# 
```
- Three files are usually required for generating chroot and nochroot packages. Three files are usually required:
  - `(focal|jammy|rhel8|rhel9)-Gluu-CE-Chroot.tgz`
  - `(rhel|suse|ubuntu)-gluu-server-nochroot.tgz`
  - `packaging-tools`
* Now copy the relevant files to the destination packaging server. Change the names of destination servers to your server name.
Relevant examples are:

- Ubuntu Focal
```
scp /backup/packages/{focal-Gluu-CE-Chroot.tgz,packaging-tools.tgz,ubuntu-gluu-server-nochroot.tgz}  root@ganesh-at-wiw-vital-raptor.gluu.info:~/
```
  - Ubuntu Jammy
```
scp /backup/packages/{jammy-Gluu-CE-Chroot.tgz,packaging-tools.tgz,ubuntu-gluu-server-nochroot.tgz}  ganesh-at-wiw-settling-ewe.gluu.info:~/
```
  - RedHat RHEL8
```
scp /backup/packages/{rhel8-Gluu-CE-Chroot.tgz,packaging-tools.tgz,rhel-gluu-server-nochroot.tgz}  root@ganesh-at-wiw-apparent-pigeon.gluu.info:~/
```
  - RedHat RHEL9
```
scp /backup/packages/{rhel9-Gluu-CE-Chroot.tgz,packaging-tools.tgz,rhel-gluu-server-nochroot.tgz}  root@ganesh-at-wiw-novel-pegasus.gluu.info:~/
```
  - Suse
```
scp /backup/packages/{packaging-tools.tgz,suse-gluu-server-nochroot.tgz}  root@ganesh-at-wiw-lucky-mudfish.gluu.info:~/
```
### Example package generation
- One example of generating packages is as below. Case is taken for Jammy.
  Copy files:
```
root@central:~# scp /backup/packages/{jammy-Gluu-CE-Chroot.tgz,packaging-tools.tgz,ubuntu-gluu-server-nochroot.tgz}  ganesh-at-wiw-sharing-panther.gluu.info:~/
jammy-Gluu-CE-Chroot.tgz                                                                                                                                    100%  491MB  79.7MB/s   00:06    
packaging-tools.tgz                                                                                                                                         100%   35KB   9.5MB/s   00:00    
ubuntu-gluu-server-nochroot.tgz                                                                                                                             100% 4966     2.0MB/s   00:00    
root@central:~# 
``` 
  - Confirm files at destination server.
```
root@ganesh-at-wiw-sharing-panther:~# ls
jammy-Gluu-CE-Chroot.tgz  packaging-tools.tgz  snap  ubuntu-gluu-server-nochroot.tgz
root@ganesh-at-wiw-sharing-panther:~# 
```
  - Unpackage the file: packaging-tools.tgz
```
root@ganesh-at-wiw-sharing-panther:~# tar xvfz packaging-tools.tgz
packaging-tools/
packaging-tools/make_key.sh
packaging-tools/copy_suse_rpm_to_repo.sh
.
.
.
packaging-tools/.config/
packaging-tools/.config/gh/hosts.yml
root@ganesh-at-wiw-sharing-panther:~# 
```
  - Change the directory to `packaging-tools`
```
root@ganesh-at-wiw-sharing-panther:~# cd packaging-tools/
root@ganesh-at-wiw-sharing-panther:~/packaging-tools# ls
chroot_version.txt        copy_suse_rpm_to_repo.sh    generate_instructions.sh  make_key.sh           nohup.out                prepare_suse_machine.sh    run_jail.sh
copy_rhel_rpm_to_repo.sh  copy_ubuntu_deb_to_repo.sh  jenkins.user.home.dir     nochroot_version.txt  prepare_rhel_machine.sh  prepare_ubuntu_machine.sh
root@ganesh-at-wiw-sharing-panther:~/packaging-tools# 
```
  - Set the versions of the packages to be generated. Version is major version and subversion is minor version. For example, `4.5.7-1` has `4.5.7` as major version and `1` as minor version. Update the values inside two files `chroot_version.txt` and `nochroot_version.txt`.
```
root@ganesh-at-wiw-sharing-panther:~/packaging-tools# cat chroot_version.txt 
version=4.5.7
subversion=1
root@ganesh-at-wiw-sharing-panther:~/packaging-tools# 
```
```
root@ganesh-at-wiw-sharing-panther:~/packaging-tools# cat nochroot_version.txt 
version=4.5.7
subversion=1
root@ganesh-at-wiw-sharing-panther:~/packaging-tools# 
```
  - Run the machine preparation script which will make it ready to build packages.
```
root@ganesh-at-wiw-sharing-panther:~/packaging-tools# sh -x ./prepare_ubuntu_machine.sh 
+ [  = refresh ]
+ grep UBUNTU_CODENAME /etc/os-release
+ cut -d= -f2
+ codename=jammy
+ cd /root/
+ files=ubuntu-gluu-server-nochroot.tgz jammy-Gluu-CE-Chroot.tgz
+ ls -1 ubuntu-gluu-server-nochroot.tgz jammy-Gluu-CE-Chroot.tgz
+ [ ! -f jammy-Gluu-CE-Chroot.tgz ]
+ [ ! -f ubuntu-gluu-server-nochroot.tgz ]
+ ls -1 ubuntu-gluu-server-nochroot.tgz jammy-Gluu-CE-Chroot.tgz
+ tar xfz jammy-Gluu-CE-Chroot.tgz
+ tar xfz ubuntu-gluu-server-nochroot.tgz
+ sudo apt update
.
.
.
Hit:8 http://security.ubuntu.com/ubuntu jammy-security InRelease                  
Reading package lists... Done                               
'/root/packaging-tools/.config/gh/hosts.yml' -> '/root/.config/gh/hosts.yml'
+ [ ! -d /home/jenkins ]
+ tar xfz /root/jammy-Gluu-CE-Chroot.tgz
+ tar xfz /root/ubuntu-gluu-server-nochroot.tgz
+ useradd -d /home/jenkins jenkins
+ cp -frv /root/packaging-tools/jenkins.user.home.dir /home/jenkins
'/root/packaging-tools/jenkins.user.home.dir' -> '/home/jenkins'
.
.
.
'/root/packaging-tools/jenkins.user.home.dir/.ssh/known_hosts' -> '/home/jenkins/.ssh/known_hosts'
'/root/packaging-tools/jenkins.user.home.dir/.ssh/authorized_keys' -> '/home/jenkins/.ssh/authorized_keys'
+ chown -R jenkins:jenkins /home/jenkins
+ mv /root/Gluu-CE-Chroot /home/jenkins/
+ sh /root/packaging-tools/generate_instructions.sh 22.04
```
  - Some files are created as shown below. 
```
root@ganesh-at-wiw-sharing-panther:~# ls
gluu-server-nochroot  jammy-Gluu-CE-Chroot.tgz  jammy.sh  packaging-tools  packaging-tools.tgz  snap  ubuntu-gluu-server-nochroot.tgz
root@ganesh-at-wiw-sharing-panther:~# ls -l jammy.sh 
-rw-r--r-- 1 root root 2189 Jan 31 17:23 jammy.sh
root@ganesh-at-wiw-sharing-panther:~# ls /home/jenkins/
.bash_history            .bashrc                  .cloud-locale-test.skip  .profile                 .ssh.old/                .wget-hsts               
.bash_logout             .cache/                  .config/                 .ssh/                    .viminfo                 Gluu-CE-Chroot/          
root@ganesh-at-wiw-sharing-panther:~# 
```
You'll notice that the file `jammy.sh` is created for `jammy` machine, `focal.sh` for `focal` machine, `rhel8.sh` for `rhel8` machine and `rhel9.sh` for `rhel9`. Nothing for suse as we don't generate chroot packages for Suse. Similarly, chroot environment is created at `/home/jenkins/Gluu-CE-Chroot`.

#### Preparing Chroot packages
To create new chroot package run below command after being in `/root`:
```
root@ganesh-at-wiw-sharing-panther:~# cd ~/
root@ganesh-at-wiw-sharing-panther:~# nohup sh -x jammy.sh &
[1] 52662
root@ganesh-at-wiw-sharing-panther:~# nohup: ignoring input and appending output to 'nohup.out'

root@ganesh-at-wiw-sharing-panther:~# 
```
Run the script as above in background and then you can tail `nohup.out` to monitor where the package building is going. The content looks something like this:
```
+ date +%a, %d %b %Y %H:%M:%S %z
+ DATE_TODAY=Fri, 31 Jan 2025 17:45:52 +0000
+ rm -fr /home/jenkins/Gluu-CE-Ub22.04
+ mkdir /home/jenkins/Gluu-CE-Ub22.04/
+ rm -fv /home/jenkins/Gluu-CE-Chroot/gluu-ce-deb-40/gluu-server/.autorelabel
+ cp -pPr /home/jenkins/Gluu-CE-Chroot/gluu-ce-deb-40 /home/jenkins/Gluu-CE-Ub22.04/
+ rm -fr /root/gluu4
+ git clone --filter blob:none --no-checkout https://github.com/GluuFederation/gluu4
Cloning into 'gluu4'...
+ cd gluu4
+ git sparse-checkout init --cone
+ git checkout 4.5
.
.
.
dpkg-source: warning: Version number suggests Ubuntu changes, but Maintainer: does not have Ubuntu address
dpkg-source: warning: native package version may not have a revision
dpkg-source: warning: source directory 'gluu-server.amd64' is not <sourcepackage>-<upstreamversion> 'gluu-server-4.5.7'
dpkg-source: info: using source format '1.0'
dpkg-source: info: building gluu-server in gluu-server_4.5.7-1~ubuntu22.04.tar.gz
.
.
.
make[1]: Entering directory '/home/jenkins/Gluu-CE-Ub22.04/gluu-ce-deb-40/gluu-server.amd64'
dh_install
dh_installdeb: warning: Compatibility levels before 10 are deprecated (level 9 in use)
   dh_gencontrol
   dh_md5sums
   dh_builddeb
dpkg-deb: building package 'gluu-server' in '../gluu-server_4.5.7-1~ubuntu22.04_amd64.deb'.
 dpkg-genbuildinfo -O../gluu-server_4.5.7-1~ubuntu22.04_amd64.buildinfo
 dpkg-genchanges -O../gluu-server_4.5.7-1~ubuntu22.04_amd64.changes
dpkg-genchanges: info: including full source code in upload
 dpkg-source --after-build .
dpkg-buildpackage: info: full upload; Debian-native package (full source is included)
./deb-build.sh: line 13: popd: directory stack empty
+ exit
```
You can confirm from below that the package is generated. In our example it is: `gluu-server_4.5.7-1~ubuntu22.04_amd64.deb`
```
root@ganesh-at-wiw-sharing-panther:~# ls /home/jenkins/Gluu-CE-Ub22.04/gluu-ce-deb-40/
gluu-server.amd64                    gluu-server_4.5.7-1~ubuntu22.04.tar.gz           gluu-server_4.5.7-1~ubuntu22.04_amd64.changes
gluu-server_4.5.7-1~ubuntu22.04.dsc  gluu-server_4.5.7-1~ubuntu22.04_amd64.buildinfo  gluu-server_4.5.7-1~ubuntu22.04_amd64.deb
root@ganesh-at-wiw-sharing-panther:~# 
```

#### Preparing No-Chroot packages
To prepare nochroot packages we need to follow below procedure.
  Go to `~/gluu-server-nochroot/` and run the `run-build.sh` in the background as below:
```
root@ganesh-at-wiw-sharing-panther:~# cd gluu-server-nochroot/
root@ganesh-at-wiw-sharing-panther:~/gluu-server-nochroot# ls
etc  opt  run-build.sh  usr
root@ganesh-at-wiw-sharing-panther:~/gluu-server-nochroot# nohup sh -x run-build.sh &
[2] 54567
root@ganesh-at-wiw-sharing-panther:~/gluu-server-nochroot# nohup: ignoring input and appending output to 'nohup.out'

root@ganesh-at-wiw-sharing-panther:~/gluu-server-nochroot# tail -f nohup.out 
+ versions_file=/root/packaging-tools/nochroot_version.txt
+ grep -w version /root/packaging-tools/nochroot_version.txt
+ cut -d= -f2
+ version=4.5.7
+ echo 4.5.7
+ cut -c1-3
+ major_package_version=4.5
+ grep -w subversion /root/packaging-tools/nochroot_version.txt
+ cut -d= -f2
+ subversion=1
+ pwd
+ base_dir=/root/gluu-server-nochroot
+ grep UBUNTU_CODENAME /etc/os-release
.
.
.
Your branch is up to date with 'origin/4.5'.
+ git sparse-checkout set packaging community-edition-setup community-edition-package
+ cd ..
+ mv /root/gluu-server-nochroot/gluu4/packaging/deb/jammy/nochroot.debian debian
+ create_deb_changelog
+ echo gluu-server-nochroot (4.5.7-1) jammy; urgency=medium
.
.
.
W: gluu-server-nochroot: unusual-interpreter python [install/community-edition-setup/static/scripts/import3031.py]
W: gluu-server-nochroot: unusual-interpreter pyton [install/community-edition-setup/setup_app/pylib/npyscreen/wgFormControlCheckbox.py]
W: gluu-server-nochroot: uses-dpkg-database-directly postrm
Finished running lintian.
+ cd ..
```
You can confirm from below that the package is generated. In our example it is: `gluu-server-nochroot_4.5.7-1_amd64.deb`
```
cd ~/gluu-server-nochroot/
root@ganesh-at-wiw-sharing-panther:~/gluu-server-nochroot# ls
debian                                          gluu-server-nochroot_4.5.7-1.debian.tar.xz  gluu-server-nochroot_4.5.7-1_amd64.buildinfo  gluu4         usr
etc                                             gluu-server-nochroot_4.5.7-1.dsc            gluu-server-nochroot_4.5.7-1_amd64.changes    nohup.out
gluu-server-nochroot-4.5.7-1                    gluu-server-nochroot_4.5.7-1.tar.gz         gluu-server-nochroot_4.5.7-1_amd64.deb        opt
gluu-server-nochroot-dbgsym_4.5.7-1_amd64.ddeb  gluu-server-nochroot_4.5.7-1_amd64.build    gluu-server-nochroot_4.5.7.orig.tar.gz        run-build.sh
```

#### Publishing the package
First move to `packaging-tools` directory and list the directories.
```
root@ganesh-at-wiw-sharing-panther:~# cd ~/packaging-tools/
root@ganesh-at-wiw-sharing-panther:~/packaging-tools# ls
chroot_version.txt        copy_suse_rpm_to_repo.sh    generate_instructions.sh  make_key.sh           nohup.out                prepare_suse_machine.sh    run_jail.sh
copy_rhel_rpm_to_repo.sh  copy_ubuntu_deb_to_repo.sh  jenkins.user.home.dir     nochroot_version.txt  prepare_rhel_machine.sh  prepare_ubuntu_machine.sh
```
To publish the OS specific packages we use copy script. In our case it's going to be ubuntu script: `copy_ubuntu_deb_to_repo.sh`.
Running this script alone shows the way to run the script. First argument is whether the package to be pushed is `chroot` or `nochroot`. Second argument is whether the package is to be pushed to `prod` or `dev` repo. The last option is `push`, which means if we want to push or not. In case, we miss the last option, only example run will be shown, but the package will be not be published.
```
root@ganesh-at-wiw-sharing-panther:~/packaging-tools# ./copy_ubuntu_deb_to_repo.sh 
Run as:
copy_ubuntu_deb_to_repo.sh [chroot|nochroot] [prod|dev] [push]
Default: ./copy_to_prod.sh nochroot dev


Your options are:
Package type: nochroot
Meant for: Development and QA
Want to push or not?: no
```
```
root@ganesh-at-wiw-sharing-panther:~/packaging-tools# ./copy_ubuntu_deb_to_repo.sh chroot dev
Run as:
copy_ubuntu_deb_to_repo.sh [chroot|nochroot] [prod|dev] [push]
Default: ./copy_to_prod.sh nochroot dev


Your options are:
Package type: chroot
Meant for: Development and QA
Want to push or not?: no
```
```
root@ganesh-at-wiw-sharing-panther:~/packaging-tools# ./copy_ubuntu_deb_to_repo.sh nochroot dev
Run as:
copy_ubuntu_deb_to_repo.sh [chroot|nochroot] [prod|dev] [push]
Default: ./copy_to_prod.sh nochroot dev


Your options are:
Package type: nochroot
Meant for: Development and QA
Want to push or not?: no
root@ganesh-at-wiw-sharing-panther:~/packaging-tools# 
```
Since packages are already pushed by this time, so we're showing only example run options.
If you want to push, you should run the command:
```
./copy_ubuntu_deb_to_repo.sh nochroot dev push
```
Or
```
./copy_ubuntu_deb_to_repo.sh nochroot dev push
```
