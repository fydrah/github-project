@Library('python')
import org.automation.*

def python = new Python()

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
      "--name", "${PROJECT_NAME}",
      "--git", "${PROJECT_GIT}",
      "--insecure"
    ])
  }
}
