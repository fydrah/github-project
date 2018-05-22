package org.automation

/**
 * Install requirements in current directory
 */
def pipInstallRequirements(requirementsFilePath) {
  sh (
    script: "pip install -r ${requirementsFilePath}"
  )
}

/**
 * Execute python scripts
 */
def execScript(script, args) {
  sh (
    script: "python ${script} ${args.join(' ')}"
  )
}

/**
 * Debug script
 */
def debugScript(args) {
  sh (
    script: "${args.join(' ')}"
  )
}
