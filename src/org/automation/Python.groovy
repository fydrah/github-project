package org.automation

/**
 * Install requirements in current directory
 */
def pipInstallRequirements(String requirementsFilePath) {
  def cmdFull = [
    "pip",
    "install",
    "-r",
    "${requirementsFilePath}"
  ]
  cmdFull.execute()
}

/**
 * Execute python scripts
 */
def exec(String script, String[] args) {
  def cmdExec = ["python", "${script}"]
  def cmdFull = cmdExec + args
  cmdFull.execute()
}
