package org.automation

/**
 * Install requirements in current directory
 */
def pipInstallRequirements(String requirementsFilePath) {
  sh (
    script: "pip install -r ${requirementsFilePath}"
  )
}

/**
 * Execute python scripts
 */
def exec(String script, String[] args) {
  sh (
    script: "python ${script} ${args.join(' ')}"
  )
}
