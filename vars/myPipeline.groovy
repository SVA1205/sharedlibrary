// Entry point for the shared pipeline — called from any project's Jenkinsfile
// Usage:
//   @Library('jenkins-shared-library') _
//   myPipeline(appName: 'my-service', sonarProjectKey: 'my-service', port: '8080')
def call(Map config = [:]) {
    def appName         = config.appName         ?: 'sample-app'
    def imageName       = config.imageName       ?: appName
    def sonarProjectKey = config.sonarProjectKey ?: appName
    def port            = config.port            ?: '3000'
    def registry        = config.registry        ?: ''

    pipeline {
        agent any

        environment {
            APP_NAME   = "${appName}"
            IMAGE_NAME = "${imageName}"
            BUILD_TAG  = "${env.BUILD_NUMBER}"
        }

        options {
            disableConcurrentBuilds()
        }

        stages {
            stage('Build') {
                steps {
                    script {
                        buildApp()
                    }
                }
            }

            stage('Test') {
                steps {
                    script {
                        runTests()
                    }
                }
            }

            stage('SonarQube Analysis') {
                steps {
                    script {
                        sonarScan(projectKey: sonarProjectKey)
                    }
                }
            }

            stage('Docker Build') {
                steps {
                    script {
                        dockerBuildPush(
                            imageName: env.IMAGE_NAME,
                            tag: env.BUILD_TAG,
                            registry: registry
                        )
                    }
                }
            }

            stage('Trivy Scan') {
                steps {
                    script {
                        trivyScan(
                            imageName: env.IMAGE_NAME,
                            tag: env.BUILD_TAG
                        )
                    }
                }
            }

            stage('Deploy') {
                steps {
                    script {
                        deployApp(
                            appName: env.APP_NAME,
                            imageName: env.IMAGE_NAME,
                            tag: env.BUILD_TAG,
                            port: port
                        )
                    }
                }
            }
        }

        post {
            success {
                echo "Pipeline succeeded! ${appName} is live at http://localhost:${port}"
            }
            failure {
                echo "Pipeline failed for ${appName} — check logs above."
            }
            always {
                echo "Build #${env.BUILD_NUMBER} finished with status: ${currentBuild.currentResult}"
            }
        }
    }
}
