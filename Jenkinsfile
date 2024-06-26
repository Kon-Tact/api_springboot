pipeline {
    agent any

    tools {
        maven 'Maven 3.9.8'
        git 'git'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    sh 'git --version'
                    sh 'git clone -b main https://github.com/Kon-Tact/api_springboot.git'
                }
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Run Application') {
            steps {
                sh 'java -jar target/your-app.jar &'
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            sh 'pkill -f "java -jar target/your-app.jar"'
        }
    }
}
