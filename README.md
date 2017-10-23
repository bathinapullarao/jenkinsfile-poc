`Thought process around creating a simple build pipeline`

1. Defining the pipeline as a `Jenkinsfile` in the project it self.
https://jenkins.io/doc/book/pipeline/getting-started/#defining-a-pipeline-in-scm

The advantage of this approach is, you don't need Jenkins Web UI to create the pipeline.
Instead we can directly create a pipeline project and point to the SCM URL where the project is hosted.


2. Style of Scripting in Jenkinsfile (Scripted vs. Declartive)
https://jenkins.io/blog/2016/12/19/declarative-pipeline-beta/

Declarative pipelines are recently added to Jenkins mix. Declarative style of programming in Jenkinsfile
reduces boilerplate and the most of the heavy lifting of a standard Jenkins pipeline is done via
bootstrapping that happens behind the scenes. It provides a nice model based DSL where you can 
just define the actions to be performed at each stage.

Standard stuff like checking out from SCM is already taken care of Jenkins when we use this approach.

Also, declarative style enables easy integration with Jenkins Blueocean UI in visualizing a nice 
stage wise pipeline.

The agent directive, which is required, instructs Jenkins to allocate an executor and workspace for 
the Pipeline. Without an agent directive, not only is the Declarative Pipeline not valid, it would 
not be capable of doing any work! By default the agent directive ensures that the source repository 
is checked out and made available for steps in the subsequent stages`


`Scripted Pipelines for Complex Requirements`

Where they differ however is in syntax and flexibility. Declarative limits what is available to the user with 
a more strict and pre-defined structure, making it an ideal choice for simpler continuous delivery pipelines. 
Scripted provides very few limits, insofar that the only limits on structure and syntax tend to be defined by 
Groovy itself, rather than any Pipeline-specific systems, making it an ideal choice for power-users and those
with more complex requirements.



`Creating a single branch Jenkins Pipeline Project with Github Repositories`

1. Provision a Jenkins Deployment in Kubernetes using `Jenkins.yml` in Bluemix cloud.
Note: We are unable to still use `minikube` because Jenkins must be available as a public IP. 
Minikube does not provide a `LoadBalancer` service. TODO: Figure out if there is another way.

2. Login to Jenkins using initial password by looking at Pod Logs.

3. Go to `Github` and create a `public` repo. Otherwise this approach wouldn't work.
 
4. Upload some code and create a `WebHook` or `Jenkins Integration` and provide the Jenkins URL.
{Jenkins_URL}`/github-webhook/`

6. Create a new pipeline project and use the option as `GitSCM polling` for build triggers


`Creating a single branch Jenkins Pipeline Project with GitLab`

1. Install the `Gitlab Plugin`
2. Once you do that, follow instructions in https://github.com/jenkinsci/gitlab-plugin on how to 
create a pipeline project
3. Go to GitLab (Bluemix) and create a webhook. You can take the URL from the pipeline configuration
where we chose `Build when a change is pushed to GitLab. GitLab CI Service URL: {URL}`
URL: JENKINS_URL/project/${jenkins_project_name}

`Creating a multi-branch pipeline with Jenkins`

The Multibranch Pipeline project type enables you to implement different Jenkinsfiles for different branches of the
 same project. In a Multibranch Pipeline project, Jenkins automatically discovers, manages and executes Pipelines 
 for branches which contain a Jenkinsfile in source control.


1. Just create a multibranch plugin and create a webhook for the project, similar to single branch pipeline. 
That's it..

TODO
* Pipeline with actual build logic
* Create a multi-branch pipeline for Login Service
* Create a persistence volume for jenkins deployment


`As a first step: Get Kubectl running inside docker container`


1. Install the `docker` plugin to get `docker` commands to work.
http://container-solutions.com/running-docker-in-jenkins-in-docker/


2. Get basic `kubectl` version deployed to local minikube
3. Get the basic `kubectl` config file exported and get the cluster details to docker
4. Adopt this docker script in the jenkins installation.

NOTE: The above approach was not successful yet.


`Docker in Docker`
https://jpetazzo.github.io/2015/09/03/do-not-use-docker-in-docker-for-ci/   


`How to get Docker in Docker with Kubernets`

https://applatix.com/case-docker-docker-kubernetes-part-2/

Above URL describes a Docker in Docker approach with Docker Daemon running as a seperate docker. 
We don't want to use this. Major problem that this approach is trying to solve is, when you 
make deployments of the same docker name + docker tag from different sources. This is not relevant for
our use case. 

The standard approach is to use docker out of docker (DOOD). We will use the host server docker daemon
by moutning `sock` directory to the jenkins docker. Therefore, we only need Docker CLI in the running container.


`Creating a Persistent Volume Claim with IBM`

https://console.bluemix.net/docs/containers/cs_apps.html#cs_apps

1. do a `kubectl get storageclasses` to check what are the storage classes

default                      ibm.io/ibmc-file   
ibmc-file-bronze (default)   ibm.io/ibmc-file   
ibmc-file-gold               ibm.io/ibmc-file   
ibmc-file-silver             ibm.io/ibmc-file   


2. Create a persistent volume claim  using one of the storage class
   
```
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: jenkins-home-volume-claim
  annotations:
    ## The Storage Classes are pre-defined by IBM. We are using the default one.
    volume.beta.kubernetes.io/storage-class: "ibmc-file-bronze"
spec:
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      ## This is the minimum. Anything less than 20Gi will be rounded to 20Gi. Next minimum is 40Gi.
      storage: 20Gi
```   
   
3. Mount it to the Jenkins Container

4. `Adding non-root user access to persistent storage`

Non-root users do not have write permission on the volume mount path for NFS-backed storage. 
To grant write permission, you must edit the Dockerfile of the image to create a directory on the mount path with the correct permission.

For IBM Bluemix Container Service, the default owner of the volume mount path is the owner nobody. With NFS storage,
 if the owner does not exist locally in the pod, then the nobody user is created. The volumes are set up to recognize 
 the root user in the container, which for some apps,
 is the only user inside a container. However, many apps specify a non-root user other than nobody that writes to the container mount path.



   
`Master-Slave Kubernetes Jenkins Setup`

When your build process uses containers, one virtual host can run jobs against different operating systems.
Container Engine provides ephemeral build executors, allowing each build to run in a clean environment that’s 
identical to the builds before it.

As part of the ephemerality of the build executors, the Container Engine cluster is only utilized when builds are 
actively running, leaving resources available for other cluster tasks such as batch processing jobs.
Build executors launch in seconds.
Container Engine leverages the Google global load balancer to route web traffic to your instance. The load balancer 
handles SSL termination, and provides a global IP address that routes users to your web front end on one of the fastest 
paths from the point of presence closest to your users through the Google backbone network.





1. Install `kubernetes-plugin`






`Settting up Bluemix API Key`
https://console.bluemix.net/docs/iam/apikeys.html

export BLUEMIX_API_KEY=pjpwekNnLrXNdA8betNUr_DOJ4vLO_K-pHiGkIVa1621


GITLAB ACCESS TOKEN: tTyoCm9b-GvN4TmxzCmz
BX IBM API KEY: pjpwekNnLrXNdA8betNUr_DOJ4vLO_K-pHiGkIVa1621



`Jenkins Setup`

2017.08.29:
Right now, Jenkins is installed simply as a single master with agents running inside master itself.
Jenkins is dockerized using the `./Dockerfile` which creates a docker image that extends from the 
base Jenkins image.



`Use of Jenkins APIs`

1. Gitlab Plugin

`Jenkins Remote APIs`

1. `GET THE CRUMB VALUE`
http://admin:admin@169.48.163.107:30802/crumbIssuer/api/json?pretty=true


2. `GET ALL THE INSTALLED PLUGINS
http://username:password@169.48.163.107:30802/pluginManager/api/json?depth=1&pretty=true

http://username:pwd@jenkins_server/pluginManager/api/json/prevalidateConfig?depth=1


3. How to use CURL export all the installed plugin
curl -sSL "http://usernmae:pwd@JENKINS_HOST/pluginManager/api/xml?depth=1&xpath=/*/*/shortName|/*/*/version&wrapper=plugins" | perl -pe 's/.*?<shortName>([\w-]+).*?<version>([^<]+)()(<\/\w+>)+/\1 \2\n/g'|sed 's/ /:/'

http://169.48.163.107:30038

`Single Master Configuration`

Setting the number of executors
You can specify and set the number of executors of your Jenkins master instance using a groovy script. 
By default its set to 2 executors, but you can extend the image and change it to your desired number of executors :

```
executors.groovy

import jenkins.model.*
Jenkins.instance.setNumExecutors(5)
and Dockerfile

FROM jenkins
COPY executors.groovy /usr/share/jenkins/ref/init.groovy.d/executors.groovy
```

















