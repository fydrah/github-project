package org.automation

import jenkins.model.*
import com.cloudbees.hudson.plugins.folder.*;
import com.cloudbees.hudson.plugins.folder.properties.*;
import com.cloudbees.hudson.plugins.folder.properties.FolderCredentialsProvider.FolderCredentialsProperty;
import com.cloudbees.plugins.credentials.impl.*;
import com.cloudbees.plugins.credentials.*;
import com.cloudbees.plugins.credentials.domains.*;


/**
 * Add git creds
 */
def addGitCreds(user, token, name) {
  def jenkins = Jenkins.instance
  def usernameAndPassword = new UsernamePasswordCredentialsImpl(
    CredentialsScope.GLOBAL,
    "git-creds", "Credentials for git",
    "${user}",
    "${token}"
  )
  for (folder in jenkins.getAllItems(Folder.class)) {
    if(folder.name == "${name}"){
    AbstractFolder<?> folderAbs = AbstractFolder.class.cast(folder)
    FolderCredentialsProperty property = folderAbs.getProperties().get(FolderCredentialsProperty.class)
      if(property) {
          property.getStore().addCredentials(Domain.global(), usernameAndPassword)
      } else {
          property = new FolderCredentialsProperty([usernameAndPassword])
          folderAbs.addProperty(property)
          property.getStore().addCredentials(Domain.global(), usernameAndPassword)
      }
    }
  }
}
