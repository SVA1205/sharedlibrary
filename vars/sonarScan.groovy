// Runs SonarQube analysis using the Scanner tool configured in Jenkins
// Requires: SonarQube Scanner plugin, server named 'SonarQube' in Jenkins system config
def call(Map config = [:]) {
    def projectKey  = config.projectKey ?: 'my-project'
    def scannerHome = tool 'SonarQube Scanner'

    withSonarQubeEnv('SonarQube') {
        sh """
            ${scannerHome}/bin/sonar-scanner \
              -Dsonar.projectKey=${projectKey} \
              -Dsonar.sources=. \
              -Dsonar.exclusions=node_modules/**,test/**,coverage/**
        """
    }
}
