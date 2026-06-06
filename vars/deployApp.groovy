// Deploys the image locally as a Docker container (simulates a deployment step)
// Stops and removes any existing container with the same name before starting fresh
def call(Map config = [:]) {
    def appName   = config.appName   ?: 'my-app'
    def imageName = config.imageName ?: 'my-app'
    def tag       = config.tag       ?: 'latest'
    def port      = config.port      ?: '3000'

    echo "Deploying ${imageName}:${tag} as '${appName}' on port ${port}..."
    sh """
        docker rm -f ${appName} || true
        docker run -d \
          --name ${appName} \
          -p ${port}:3000 \
          ${imageName}:${tag}
    """
    echo "App deployed — http://localhost:${port}"
}
