pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh '''
                    chmod +x gradlew
                    ./gradlew build -x test
                '''
            }
        }
        stage('DockerSize') {
            steps {
                sh '''
                    docker stop gateway || true
                    docker rm gateway || true
                    docker rmi gateway || true
                    docker build -t gateway .
                    echo "gateway: build success"
                '''
            }
        }
        stage('Deploy') {
            steps {
                sh '''
                docker run -e EUREKA_URL="${EUREKA_URL}" -d --network gentledog --name gateway -p 8000:8000 gateway'
                echo "gateway: run success"
                '''
            }
        }
    }
}