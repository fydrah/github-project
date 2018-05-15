#!/usr/bin/env python

from os import environ as env
import os,sys,subprocess
import jenkins
import requests
import argparse
import json
import jinja2
import uuid

def create_job(auth,args):
  context = {
    'jjb_server': auth['url'],
    'project_name': args.name,
    'git_url': args.git,
  }
  folder = '/tmp/jenkinsjob-%s/' % str(uuid.uuid4())
  os.makedirs(folder)
  config_file = _gen_config(args.config_template, folder+'config.ini', context)
  job_file = _gen_config(args.job_template, folder+'job.yaml', context)
  env['JJB_SECTION'] = 'jenkins'
  env['JJB_USER'] = auth['username']
  env['JJB_PASSWORD'] = auth['token']
  subprocess.call([
    'jenkins-jobs',
    '--conf', config_file,
    'update', job_file
  ])

def main():
  auth = {}
  auth['url'] = env.get('JENKINS_URL')
  auth['username'] = env.get('JENKINS_USERNAME')
  auth['token'] = env.get('JENKINS_API_TOKEN')
  if auth['url'] is None or auth['url'] == '':
    print('ERROR: Please set JENKINS_URL')
    sys.exit(1)
  if auth['username'] is None or auth['username'] == '':
    print('ERROR: Please set JENKINS_USERNAME')
    sys.exit(1)
  if auth['token'] is None or auth['token'] == '':
    print('ERROR: Please set JENKINS_API_TOKEN')
    sys.exit(1)
  parser = argparse.ArgumentParser(description='Create Jenkins Job')
  parser.add_argument('--name', required=True, help='Name of the project')
  parser.add_argument('--git', required=True, help='GitHub URL of the repository')
  parser.add_argument('--config-template', required=False, help='Configuration jinja2 template for JJB', default='templates/config_template.ini.j2')
  parser.add_argument('--job-template', required=False, help='Job jinja2 template for JJB', default='templates/job_template.yaml.j2')
  parser.add_argument('--insecure', action='store_true', required=False, help='Don\'t check certificates', default=False)
  args = parser.parse_args()
  if args.insecure:
    env['PYTHONHTTPSVERIFY'] = '0'
  if jenkins.Jenkins(auth['url'],auth['username'],auth['token']).job_exists(args.name):
    print('Job already exists. Abort.')
    sys.exit(1)
  create_job(auth, args)

def _render(tpl_path, context):
    path, filename = os.path.split(tpl_path)
    return jinja2.Environment(
        loader=jinja2.FileSystemLoader(path or './')
    ).get_template(filename).render(context)

def _gen_config(template_path, dest, context):
    gen_file = file(dest,'a')
    gen_file.write(_render(template_path, context))
    gen_file.close()
    return dest

if __name__ == '__main__':
  main()
