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
                        def result = sh(script: "ssh root@200.58.106.151 'bash /root/capnee/deploy_capnee_be.sh ${Branch}'", returnStatus: true)
                        if (result != 0) {
                            error("Error: El comando SSH para producción no se ejecutó correctamente. Código de salida: ${result}")
                        }
                    } else if (Ambiente == 'UAT') {
                        // Ejecutar el script en la VM de UAT
                        def result = sh(script: "sh /home/ubuntu/capnee/deploy_capnee_be.sh ${Branch}", returnStatus: true)
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
