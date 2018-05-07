Simple GitHub project creation script
#####################################

Requirements
============

* Install:

  .. code-block:: console

      pip install -r requirements.txt


* Configure GitHub token:

    * Create a personal access token on https://github.com/settings/tokens
    * Scopes needed: ``repo``

* Export your token:

  .. code-block:: console

      export GITHUB_API_TOKEN=mysupersecrettoken

Usage
=====

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


