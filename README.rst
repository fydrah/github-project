Simple GitHub and Jenkins project bootstrap script
##################################################

Requirements
============

* Install:

  .. code-block:: console

      pip install -r requirements.txt

Configure
=========

GitHub
------

* Configure GitHub API token:

    * Create a personal access token on https://github.com/settings/tokens
    * Scopes needed: ``repo``

* Export your token:

  .. code-block:: console

      export TARGET_GIT_TOKEN=mysupersecrettoken

Jenkins
-------

* Configure Jenkins API token:

    * Go to https://JENKINS_URL/user/YOUR_USER/configure
    * Click on `Show API Token...` or generate one

* Export vars:

  .. code-block:: console

      JENKINS_URL=JENKINS_URL
      JENKINS_API_TOKEN=YOUR_TOKEN
      JENKINS_USERNAME=YOUR_USER

* If you have some issues with certificates validation, export this var:

  .. code-block:: console

      PYTHONHTTPSVERIFY=0

  .. note::

      Currently this var is exported by default. This is a future update
      to remove this behavior.

Usage
=====

GitHub
------

.. code-block:: console

    usage: create_project.py [-h] --name NAME --type {user,org} [--org ORG]
                         [--private]

    Create Github projects
    
    optional arguments:
      -h, --help         show this help message and exit
      --name NAME        Name of the repository
      --type {user,org}  Create repository for user or organisation
      --org ORG          Organisation name. Required if type is "org"
      --private          Create private repository


Jenkins
-------

This is a simple wrapper for Jenkins Job Builder project (https://docs.openstack.org/infra/jenkins-job-builder/).
It uses jinja2 template to create a base project with some jobs.
  
.. code-block:: console

    usage: create_base_job.py [-h] --target-name PROJECT_NAME --project-git
                              TARGET_GIT [--config-template CONFIG_TEMPLATE]
                              [--job-template JOB_TEMPLATE] [--insecure]
    
    Create Jenkins Job
    
    optional arguments:
      -h, --help            show this help message and exit
      --target-name PROJECT_NAME
                            Name of the project
      --project-git TARGET_GIT
                            GitHub URL of the repository
      --config-template CONFIG_TEMPLATE
                            Configuration jinja2 template for JJB
      --job-template JOB_TEMPLATE
                            Job jinja2 template for JJB
      --insecure            Don't check certificates

Utilisation de clone_template_repo.py
-------------------------------------

.. code-block:: console

   usage: clone_template_repo.py [-h] --target-git TARGET_GIT
                                 [--target-git-user TARGET_GIT_USER] --source-git
                                 SOURCE_GIT [--source-git-user SOURCE_GIT_USER]
                                 [--insecure]

Démo déploiement factory
========================

.. code-block:: console

   oc login $URL
   oc new-project jenkins
   oc process -f templates/jenkins.yaml |oc create -f -

Puis on se connecte au jenkins pour récupérer le user et le token admin, ainsi qu'à github, pour créer le fichier de configuration suivant  (./env):

.. code-block:: console

   #!/bin/bash
   
   export JENKINS_URL="REDACTED"
   export JENKINS_API_TOKEN="REDACTED"
   export JENKINS_USERNAME="REDACTED"
   export TARGET_GIT_TOKEN="REDACTED"

Ensuite, on peut terminer le déploiement :

.. code-block:: console

   source ./env
   curl -k --user "$JENKINS_USERNAME:$JENKINS_API_TOKEN" --data-urlencode "script=$(< ./groovy-scripts/shared-library.groovy)" "${JENKINS_URL}scriptText"
   ./create_base_job.py --target-name bootstrap --target-git https://github.com/fydrah/project-setup --job-template templates/job_bootstrap.yaml.j2

Enfin, on lance le job bootstrap depuis l'interface jenkins pour boostraper un projet d'API.

Droit à ajouter au compte jenkin
--------------------------------

.. code-block:: console

   oc adm policy add-cluster-role-to-user self-provisioner system:serviceaccount:$PROJECT_NAME:default
