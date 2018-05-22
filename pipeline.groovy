@Library('python')
import org.automation.*

def python = new Python()

podTemplate(label: 'python27', cloud: 'openshift', containers: [
  containerTemplate(name: 'python27', image: 'jarou/jenkins-slave-python27-centos7', ttyEnabled: true, command: 'cat', envVars: [envVar(key: 'BASH_ENV', value: '/home/jenkins/venv/bin/activate'), envVar(key: 'ENV', value: '/home/jenkins/venv/bin/activate')])
  ]) {

    node ("python27") {
      stage("Setup"){
        deleteDir()
        checkout scm
        container('python27') {
          python.pipInstallRequirements("./requirements.txt")
        }
      }
      stage("Create GitHub Project"){
        container('python27') {
          python.execScript("create_project.py", [
            "--name", "${PROJECT_NAME}",
            "--type", "user"
          ])
        }
      }
      stage("Create Jenkins Job"){
        container('python27') {
          python.execScript("create_base_job.py", [
            "--name", "${PROJECT_NAME}",
            "--git", "${PROJECT_GIT}",
            "--insecure"
          ])
        }
      }
    }

}
