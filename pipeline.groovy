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
          "--name", "${TARGET_NAME}",
          "--type", "user"
        ])
      }
      stage("Create Jenkins Job"){
        python.execScript("create_base_job.py", [
          "--target-name", "${TARGET_NAME}",
          "--target-git", "${TARGET_GIT}",
          "--insecure"
        ])
      }
      stage("Clone the soure api repo"){
        python.execScript("clone_template_repo.py", [
          "--source-git", "${SOURCE_GIT}",
          "--source-git-user", "${SOURCE_GIT_USER}",
          "--source-git-branch", "${SOURCE_GIT_BRANCH}",
          "--target-git-user", "${TARGET_GIT_USER}",
          "--target-git", "${TARGET_GIT}",
          "--insecure"
        ])
      }
    }
}
