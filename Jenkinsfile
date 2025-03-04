pipeline {
	agent {
		label 'kub-gradle-agent'
    }

    options {
		buildDiscarder(logRotator(numToKeepStr: '5'))
        disableConcurrentBuilds()
    }

    environment {
		PROJECT_KEY='sboot-order-dispatcher'
		APP_IMAGE = 'public.ecr.aws/n1a9j0r1/sboot-order-dispatcher'
        AWS_REGION = 'us-east-1'
    }

    stages {

		stage('Build with Gradle') {
			steps {
				container('gradle') {
					sh 'gradle clean build -x test'
					sh 'cp build/libs/app.jar ./app.jar'
                }
            }
        }

        stage('Login AWS CLI ECR') {
			steps {
				container('aws-cli') {
					withCredentials([
						usernamePassword(
							credentialsId: 'aws-username-password',
							usernameVariable: 'AWS_ACCESS_KEY_ID',
							passwordVariable: 'AWS_SECRET_ACCESS_KEY'
						)
						]) {
						sh '''
							aws ecr-public get-login-password --region $AWS_REGION > ecr-login.txt
						'''
						}
				}
			}
		}

		stage('Login Buildah AWS ECR') {
			steps {
				container('buildah') {
					sh '''
						buildah login -u AWS -p $(cat ecr-login.txt) public.ecr.aws
					'''
				}
			}
		}

		stage('Login Buildah Docker Hub') {
			steps {
				container('buildah') {
					withCredentials([usernamePassword(
						credentialsId: 'docker-hub-credentials',
						usernameVariable: 'DOCKERHUB_USER',
						passwordVariable: 'DOCKERHUB_PASS'
					)]) {
						sh 'buildah login -u $DOCKERHUB_USER -p $DOCKERHUB_PASS docker.io'
					}
				}
			}
		}

    	stage('SonarQube Analysis') {
			steps {
				container('sonar-scanner') {
					withSonarQubeEnv('sonarqube-server') {
						withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
							sh '''
								sonar-scanner \
									-Dsonar.projectKey=$PROJECT_KEY \
									-Dsonar.sources=. \
									-Dsonar.java.binaries=build/classes/java/main
                    		'''
						}
					}
				}
			}
        }

    	stage('Trivy Security Scan') {
			steps {
				container('trivy') {
					sh '''
						trivy fs --skip-dirs build/static/js .
					'''
				}
			}
		}

		stage('Build Multi-Arch') {
			steps {
				container('buildah') {
					sh '''
						buildah bud --layers --platform linux/amd64 --build-arg JAR_FILE=app.jar -t ${APP_IMAGE}-amd64:latest .
						buildah bud --layers --platform linux/arm64 --build-arg JAR_FILE=app.jar -t ${APP_IMAGE}-arm64:latest .
						buildah manifest create ${APP_IMAGE}:latest --amend ${APP_IMAGE}-amd64:latest --amend ${APP_IMAGE}-arm64:latest
            		'''
            	}
        	}
		}

		stage('Push Image') {
			steps {
				container('buildah') {
					sh '''
                		buildah manifest push ${APP_IMAGE}:latest docker://${APP_IMAGE}:latest
            		'''
				}
			}
		}

	}
}
