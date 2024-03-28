pipeline{
    agent any
    environment{
        // GIT Proyect
        Branch = "${env.BRANCH}"
        Hostname = "${env.HOSTNAME}"
        // Deploy
        DeployServerUser = 'ubuntu'        
        DeployServerIP_UAT = '54.232.219.223'
        CredentialId = 'pem'
        PemFilePath = "/home/ubuntu/clave.pem"
    }

    stages{
        stage("Build Info") {
            steps {
                script {
                    BUILD_TRIGGER_BY = currentBuild.getBuildCauses()[0].userId
                    currentBuild.displayName = "#${env.BUILD_NUMBER} Hostname:${Hostname} Branch:${Branch} By:${BUILD_TRIGGER_BY} "                    
                }
            }
        }    

        stage('Deploy') {
        steps {
            script {
                try {
                    // Usar las credenciales SSH configuradas en Jenkins
                    sshagent(credentials: [CredentialId]) {
                            // Comando SSH para ejecutar el script remoto
                        def result = sh(script: "ssh -i ${PemFilePath} ubuntu@54.232.219.223 sh /home/ubuntu/estacionamiento/deploy_estacionamiento_be.sh ${Branch}", returnStatus: true)

                        // Verificar el código de salida del comando SSH
                        if (result != 0) {
                            error("Error: El comando SSH no se ejecutó correctamente. Código de salida: ${result}")
                        }
                    }
                } catch (Exception ex) {
                    // Capturar cualquier excepción y manejarla
                    error("Error durante la ejecución del script: ${ex.message}")
                }
            }
            }
        }
    }
}

