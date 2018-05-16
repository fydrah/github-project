import org.automation.*

def python = new Python()

node ("python2.7") {
  stage("Setup"){
    deleteDir()
    checkout scm
    python.pipInstallRequirements("./requirements.txt")
  }
  stage ("Create GitHub Project") {
    python.exec("create_project.py", [
      "--name", "${PROJECT_NAME}",
      "--type", "user"
    ])
  }
  stage ("Create Jenkins Job") {
    python.exec("create_base_job.py", [
      "--name", "${PROJECT_NAME}",
      "--git", "${PROJECT_GIT}"
    ])
  }
}
