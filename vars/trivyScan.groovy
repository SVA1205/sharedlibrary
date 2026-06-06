// Scans a Docker image for vulnerabilities using Trivy
// --exit-code 0 means pipeline continues even if vulns are found (change to 1 to fail on CRITICAL)
def call(Map config = [:]) {
    def imageName = config.imageName ?: 'my-app'
    def tag       = config.tag       ?: 'latest'
    def severity  = config.severity  ?: 'HIGH,CRITICAL'
    def exitCode  = config.exitCode  ?: '0'

    echo "Scanning ${imageName}:${tag} with Trivy (severity: ${severity})..."
    sh """
        trivy image \
          --exit-code ${exitCode} \
          --severity ${severity} \
          --format table \
          --no-progress \
          ${imageName}:${tag}
    """
}
