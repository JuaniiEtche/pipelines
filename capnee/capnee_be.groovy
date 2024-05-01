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
                    // Usar diferentes scripts de deployment basados en el valor de Ambiente
                    if (Ambiente == 'PROD') {
                        // Ejecutar el script en la VM de producción
                        withCredentials([usernamePassword(credentialsId: 'clave', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                            def result = sh(script: "sshpass -p '${PASSWORD}' ssh -o StrictHostKeyChecking=no -p 5948 ${USERNAME}@200.58.106.151 'bash /root/capnee/deploy_capnee_be.sh ${Branch}'", returnStatus: true)
                            if (result != 0) {
                                error("Error: El comando SSH para PROD no se ejecutó correctamente. Código de salida: ${result}")
                            }
                    }
                    } else if (Ambiente == 'UAT') {
                        // Ejecutar el script en la VM de UAT
                        def result = sh(script: "sh /home/administrador/projects/capnee/deploy_capnee_be.sh ${Branch}", returnStatus: true)
                        if (result != 0) {
                            error("Error: El comando SSH para UAT no se ejecutó correctamente. Código de salida: ${result}")
                        }
                    } else {
                        error("Error: Valor de ambiente no reconocido '${Ambiente}'")
                    }
                }
            }
        }
    }
}
