pipeline {
    agent any

    tools {
        // Installe la version de Maven nécessaire
        maven 'Maven 3.6.3' // Assurez-vous que cette version est installée et configurée dans Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                // Cloner le dépôt Git
                git branch: 'main', url: 'https://github.com/your-repo/your-spring-boot-project.git'
            }
        }
        stage('Build') {
            steps {
                // Construire le projet avec Maven
                sh 'mvn clean package'
            }
        }
        stage('Test') {
            steps {
                // Lancer les tests unitaires
                sh 'mvn test'
            }
        }
        stage('Run Application') {
            steps {
                // Démarrer l'application Spring Boot pour vérifier qu'elle se lance correctement
                sh 'java -jar target/your-app.jar &'
                // Vous pouvez ajouter des vérifications supplémentaires ici pour vous assurer que l'application s'est bien lancée
            }
        }
    }

    post {
        always {
            // Archive les résultats des tests
            junit '**/target/surefire-reports/*.xml'
            // Arrêter l'application Spring Boot
            sh 'pkill -f "java -jar target/your-app.jar"'
        }
    }
}
