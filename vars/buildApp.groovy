// Called as buildApp() from any pipeline — installs Node.js dependencies
def call(Map config = [:]) {
    echo "Installing dependencies..."
    sh 'npm ci'
}
