#!/usr/bin/env python

from os import environ as env
import sys
import requests
import argparse
import json

def create_project(headers,args):
  # https://developer.github.com/v3/repos/#create
  headers['content-type'] = 'application/json'
  data = json.dumps({
    'name': args.name,
    'private': args.private,
  })
  if args.type == 'user':
    path = '/user/repos'
  else:
    if args.org is None or args.org == '':
      print('ERROR: "--org NAME" is required if type is "org"') 
      sys.exit(1)
    path = '/orgs/%s/repos' % args.org
  try:
    resp = requests.post('https://api.github.com'+path, headers=headers, data=data)
    print('OK: %s' % resp.text)
  except Exception as e:
    print('Could not create repository: %s' % str(e))
  

def main():
  token_auth = env.get('TARGET_GIT_TOKEN')
  if token_auth is None or token_auth == '':
    print('ERROR: Please set TARGET_GIT_TOKEN')
    sys.exit(1)
  auth_header = {'Authorization': 'token %s' % env.get('TARGET_GIT_TOKEN')}
  parser = argparse.ArgumentParser(description='Create Github projects')
  parser.add_argument('--name', required=True, help='Name of the repository')
  parser.add_argument('--type', choices=['user','org'], required=True, help='Create repository for user or organisation')
  parser.add_argument('--org', help='Organisation name. Required if type is "org"')
  parser.add_argument('--private', action='store_true', help='Create private repository', required=False, default=False)
  args = parser.parse_args()
  create_project(auth_header, args)
  

if __name__ == '__main__':
  main()
