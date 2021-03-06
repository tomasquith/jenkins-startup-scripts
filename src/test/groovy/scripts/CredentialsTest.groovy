package scripts

import jenkins.model.Jenkins
import org.junit.BeforeClass
import org.junit.Test
import org.jvnet.hudson.test.recipes.LocalData
import org.jvnet.hudson.test.recipes.WithPlugin
import utilities.ZipTestFiles

import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat

class CredentialsTest extends StartupTest {

    @BeforeClass
    public static void setUp() {
        setUp(CredentialsTest.class, ["scripts/credentials.groovy"])
    }

    @Test
    @LocalData
    @WithPlugin(["hashicorp-vault-plugin-2.1.0.hpi", "hashicorp-vault-credentials-plugin-0.0.9.hpi", "credentials-binding-1.10.hpi",
            "credentials-2.1.16.hpi", "workflow-api-2.26.hpi", "workflow-step-api-2.14.hpi", "structs-1.10.hpi", "plain-credentials-1.4.hpi",
            "ssh-credentials-1.13.hpi", "sauce-ondemand-1.164.hpi", "maven-plugin-2.17.hpi", "matrix-project-1.12.hpi",
            "workflow-basic-steps-2.5.hpi", "run-condition-1.0.hpi", "workflow-cps-2.23.hpi", "junit-1.23.hpi", "workflow-job-2.16.hpi",
            "script-security-1.40.hpi", "javadoc-1.1.hpi", "token-macro-2.3.hpi",
            "workflow-scm-step-2.6.hpi", "workflow-support-2.16.hpi", "ace-editor-1.0.1.hpi", "jquery-detached-1.2.1.hpi",
            "scm-api-2.2.6.hpi", "workflow-cps-2.23.hpi", "gitlab-plugin-1.4.8.hpi", "git-3.7.0.hpi", "git-client-2.7.0.hpi",
            "cloudbees-folder-6.1.0.hpi", "apache-httpcomponents-client-4-api-4.5.3-2.1.hpi", "jsch-0.1.54.1.hpi", "display-url-api-2.2.0.hpi",
            "mailer-1.20.hpi"])
    @ZipTestFiles(files = ["jenkins.config"])
    void shouldConfigureCredentialsFromConfig() {

        def usernamePasswordCredentials = getCredentialsOfType("UsernamePasswordCredentialsImpl")
        assertThat(usernamePasswordCredentials[0].getUsername() as String, equalTo("repository"))
        assertThat(usernamePasswordCredentials[0].getPassword() as String, equalTo("somes3cret"))
        assertThat(usernamePasswordCredentials[1].getUsername() as String, equalTo("cloud"))
        assertThat(usernamePasswordCredentials[1].getPassword() as String, equalTo("cl0ud"))

        def basicSSHUserPrivateKey = getCredentialsOfType("BasicSSHUserPrivateKey")
        assertThat(basicSSHUserPrivateKey[0].getUsername() as String, equalTo("ssh"))
        assertThat(basicSSHUserPrivateKey[0].getPassphrase() as String, equalTo("cl0ud"))
        assertThat(basicSSHUserPrivateKey[0].getPrivateKeySource().getPrivateKeyFile() as String, equalTo(".ssh/id_rsa"))

        def credentials = getCredentialsOfType("HashicorpVaultCredentialsImpl")
        assertThat(credentials[0].getKey() as String, equalTo("super/secret"))
        assertThat(credentials[0].getUsernameKey() as String, equalTo("username"))
        assertThat(credentials[0].getPasswordKey() as String, equalTo("password"))
        assertThat(credentials[0].getDescription() as String, equalTo("vault credentials"))

        def sauceLabsCredentials = getCredentialsOfType("SauceCredentials")
        assertThat(sauceLabsCredentials[0].getUsername() as String, equalTo("slUser"))
        assertThat(sauceLabsCredentials[0].getApiKey() as String, equalTo("slApiKey"))
        assertThat(sauceLabsCredentials[0].getDescription() as String, equalTo("SauceLabs credentials"))

        def gitlabCredentials = getCredentialsOfType("GitLabApiToken")
        assertThat(gitlabCredentials[0].getApiToken() as String, equalTo("somes3cret"))
        assertThat(gitlabCredentials[0].getDescription() as String, equalTo("Gitlab credentials"))

        def stringCredentials = getCredentialsOfType("StringCredential")
        assertThat(stringCredentials[0].getSecret() as String, equalTo("somestring"))
        assertThat(stringCredentials[0].getDescription() as String, equalTo("auth token"))
    }

    private getCredentialsOfType(String type) {
        def provider = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0]
        provider.getCredentials().findAll {
            it.class.toString().contains(type)
        }
    }
}
