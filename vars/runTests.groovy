// Runs Jest tests with coverage
def call(Map config = [:]) {
    echo "Running tests..."
    sh 'npm test'
}
