// Builds a Docker image; optionally pushes to a registry
// Uses the Docker socket mounted in the Jenkins container
def call(Map config = [:]) {
    def imageName = config.imageName ?: 'my-app'
    def tag       = config.tag       ?: 'latest'
    def registry  = config.registry  ?: ''

    def fullImage = registry ? "${registry}/${imageName}:${tag}" : "${imageName}:${tag}"

    echo "Building Docker image: ${fullImage}"
    sh "docker build -t ${fullImage} ."

    if (registry) {
        withCredentials([usernamePassword(
            credentialsId: 'docker-registry-creds',
            usernameVariable: 'DOCKER_USER',
            passwordVariable: 'DOCKER_PASS'
        )]) {
            sh "echo \$DOCKER_PASS | docker login ${registry} -u \$DOCKER_USER --password-stdin"
            sh "docker push ${fullImage}"
        }
    }
}
