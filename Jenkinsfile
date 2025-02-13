pipeline {
	agent any
    stages {
		stage('Checkout') {
			steps {
				git branch: 'main', url: 'https://github.com/thoggs/sboot-order-dispatcher.git'
            }
        }
        stage('Build') {
			steps {
				sh 'docker build -t sboot-order-dispatcher:latest .'
            }
        }
    }
}