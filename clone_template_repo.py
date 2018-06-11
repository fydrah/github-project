#!/usr/bin/env python

from os import environ as env
import os,sys,subprocess
import argparse
import shutil
from urlparse import urlparse

tmp_target = '/tmp/tmp-git/'

def create_git_url(target, args):
  if 'SOURCE' in target:
    url = urlparse(args.source_git)
  else:
    url = urlparse(args.target_git)
  git_token = env.get(target, None)
  if git_token:
    if 'SOURCE' in target:
      repo = url.scheme + '://' + args.source_git_user + ':' + git_token + '@' + url.netloc  + url.path
    else:
      repo = url.scheme + '://' + args.target_git_user + ':' + git_token + '@' + url.netloc  + url.path
  else:
    if 'SOURCE' in target:
      repo = args.source_git
    else:
      repo = args.target_git
  return repo

def create_template(repo, tmp_target, args):
  subprocess.call('git clone %s %s' % (repo, tmp_target), shell=True)
  os.chdir(tmp_target)
  subprocess.call('git checkout %s' % (args.source_git_branch), shell=True)
  shutil.rmtree(os.path.join(tmp_target, '.git'))

def push_git_cible(repo, tmp_target, args):
  os.chdir(tmp_target)
  subprocess.call('git init', shell=True)
  subprocess.call('git config user.email "jenkins@ci.local"', shell=True)
  subprocess.call('git config user.name "jenkins"', shell=True)
  subprocess.call('git remote add origin %s' % (repo), shell=True)
  subprocess.call('git add -f *', shell=True)
  subprocess.call('git commit -m "first commit"', shell=True)
  subprocess.call('git push -u origin master', shell=True)

def main():
  parser = argparse.ArgumentParser(description='Create Jenkins Job')
  parser.add_argument('--target-git', required=True)
  parser.add_argument('--target-git-user', required=False, default=False)
  parser.add_argument('--source-git', required=True)
  parser.add_argument('--source-git-branch', required=True)
  parser.add_argument('--source-git-user', required=False, default=False)
  parser.add_argument('--insecure', action='store_true', required=False, help='Don\'t check certificates', default=False)
  args = parser.parse_args()
  if args.insecure:
    env['PYTHONHTTPSVERIFY'] = '0'
  source_repo = create_git_url('SOURCE_GIT_TOKEN', args)
  create_template(source_repo, tmp_target, args)
  target_repo = create_git_url('TARGET_GIT_TOKEN', args)
  push_git_cible(target_repo, tmp_target, args)

if __name__ == '__main__':
  main()

