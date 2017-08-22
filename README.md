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

1. Install the `Gitlab Plugin
2. Once you do that, follow instructions in https://github.com/jenkinsci/gitlab-plugin on how to 
create a pipeline project
3. Go to GitLab (Bluemix) and create a webhook. You can take the URL from the pipeline configuration
where we chose `Build when a change is pushed to GitLab. GitLab CI Service URL: {URL}`


`Creating a multi-branch pipeline with Jenkins`

The Multibranch Pipeline project type enables you to implement different Jenkinsfiles for different branches of the
 same project. In a Multibranch Pipeline project, Jenkins automatically discovers, manages and executes Pipelines 
 for branches which contain a Jenkinsfile in source control.


1. Just create a multibranch plugin and create a webhook for the project, similar to single branch pipeline. 
That's it..

TODO
* Pipeline with actual build logic
