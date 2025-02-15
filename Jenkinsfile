pipeline {

	agent {
		label 'ec2-agent'
    }

    options {
		disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5'))
        skipDefaultCheckout(true)
    }

    triggers {
		githubPush()
    }

    environment {
		DOCKER_IMAGE = 'public.ecr.aws/n1a9j0r1/sboot-order-dispatcher'
        AWS_REGION = 'us-east-1'
    }

    stages {

		stage('Checkout') {
			steps {
				git branch: 'main', url: 'https://github.com/thoggs/sboot-order-dispatcher.git'
            }
        }

        stage('Set up QEMU') {
			steps {
				sh 'docker run --rm --privileged multiarch/qemu-user-static --reset -p yes || true'
    		}
		}

        stage('Set up Docker Buildx') {
			steps {
				sh 'docker buildx create --use --name mybuilder || true'
                sh 'docker buildx inspect --bootstrap'
            }
        }

        stage('Login to AWS ECR Public') {
			steps {
				withCredentials([
                    usernamePassword(
                        credentialsId: 'aws-username-password',
                        usernameVariable: 'AWS_ACCESS_KEY_ID',
                        passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                    )
                ]) {
					sh '''
                        aws ecr-public get-login-password --region $AWS_REGION \
                        | docker login --username AWS --password-stdin public.ecr.aws
                    '''
                }
            }
        }

        stage('Build amd64') {
			steps {
				sh '''
                    docker buildx build \
                      --platform linux/amd64 \
                      -t $DOCKER_IMAGE-amd64 \
                      --load .
                '''
            }
        }

        stage('Build arm64') {
			steps {
				sh '''
                    docker buildx build \
                      --platform linux/arm64 \
                      -t $DOCKER_IMAGE-arm64 \
                      --load .
                '''
            }
        }

		stage('Create Multi-Arch Manifest') {
			steps {
				sh '''
					docker manifest create $DOCKER_IMAGE:latest \
						--amend $DOCKER_IMAGE-amd64 \
						--amend $DOCKER_IMAGE-arm64
				'''
			}
		}

		stage('Push Multi-Arch Manifest') {
			steps {
				sh '''
					docker manifest push $DOCKER_IMAGE:latest
				'''
			}
		}

    }
}
