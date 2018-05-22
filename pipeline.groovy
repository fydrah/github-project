@Library('python')
import org.automation.*

def python = new Python()

podTemplate(label: 'python27', cloud: 'openshift', containers: [
  containerTemplate(
    name: 'jnlp',
    image: 'fhardy/jenkins-slave-python27-centos7',
    alwaysPullImage: true,
    ttyEnabled: true,
    workingDir: '/tmp',
    args: '${computer.jnlpmac} ${computer.name}'
  )
]) {

    node ("python27") {
      stage("Setup"){
        deleteDir()
        checkout scm
        python.pipInstallRequirements("./requirements.txt")
      }
      stage("Create GitHub Project"){
        python.execScript("create_project.py", [
          "--name", "${PROJECT_NAME}",
          "--type", "user"
        ])
      }
      stage("Create Jenkins Job"){
        python.execScript("create_base_job.py", [
          "--project-name", "${PROJECT_NAME}",
          "--project-git", "${PROJECT_GIT}",
          "--insecure"
        ])
      }
    }
}
