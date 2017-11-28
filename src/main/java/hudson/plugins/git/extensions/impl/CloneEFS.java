package hudson.plugins.git.extensions.impl;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.git.GitException;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.extensions.GitClientType;
import hudson.plugins.git.extensions.GitSCMExtension;
import hudson.plugins.git.extensions.GitSCMExtensionDescriptor;
import java.io.IOException;
import java.io.File;
import java.util.List;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.jenkinsci.plugins.gitclient.CloneCommand;
import org.jenkinsci.plugins.gitclient.FetchCommand;
import org.jenkinsci.plugins.gitclient.GitClient;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * @author xiaoye zhang
 */
public class CloneEFS extends GitSCMExtension {
    private final boolean enable;

    @DataBoundConstructor
    public CloneEFS(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void decorateCloneCommand(GitSCM scm, Run<?, ?> build, GitClient git, TaskListener listener, CloneCommand cmd) throws IOException, InterruptedException, GitException {
        if (enable) {
            listener.getLogger().println("Setting repo remote to be from EFS");
            RemoteConfig rc = scm.getRepositories().get(0);
            String remoteUrl = rc.getURIs().get(0).toPrivateString();
            listener.getLogger().println(remoteUrl);
            if (remoteUrl.contains("git.dev.fwmrm.net")) {
                String efsUrl = remoteUrl.replaceAll("^.*git.dev.fwmrm.net", "/data1/efs/git_repo").replace(".git", "/.git");
                File f = new File(efsUrl);
                if (f.isDirectory()) {
                    cmd.url(efsUrl);
                } else {
                    listener.getLogger().println(efsUrl + " does not exist, go cloning from remote repo");
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public GitClientType getRequiredClient() {
        return GitClientType.GITCLI;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CloneEFS that = (CloneEFS) o;

        if (enable != that.enable) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return CloneEFS.class.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "CloneEFS{" +
                "enable=" + enable +
                '}';
    }

    @Extension
    public static class DescriptorImpl extends GitSCMExtensionDescriptor {
        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return "Clone from EFS mirror";
        }
    }

}
