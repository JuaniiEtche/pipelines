pipeline {
    agent any
    environment {
        // Variables de entorno del proyecto GIT
        Branch = "${env.BRANCH}"
        Hostname = "${env.HOSTNAME}"
        Ambiente = "${env.AMBIENTE}" 
    }

    stages {
        stage("Build Info") {
            steps {
                script {
                    BUILD_TRIGGER_BY = currentBuild.getBuildCauses()[0]?.userId ?: 'unknown'
                    currentBuild.displayName = "#${env.BUILD_NUMBER} Hostname:${Hostname} Branch:${Branch} By:${BUILD_TRIGGER_BY}"
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Ejecutar el script en la VM de UAT
                    def result = sh(script: "sh /home/administrador/projects/recordemos/deploy_recordemos_be.sh ${Branch}", returnStatus: true)
                    if (result != 0) {
                        error("Error: El comando SSH para UAT no se ejecutó correctamente. Código de salida: ${result}")
                    }
                }
            }
        }
    }
}
