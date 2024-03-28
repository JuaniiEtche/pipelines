pipeline{
    agent any
    environment{
        // GIT Proyect
        Branch = "${env.BRANCH}"
        Hostname = "${env.HOSTNAME}"
        // Deploy
        DeployServerUser = 'ubuntu'        
        DeployServerIP_UAT = '54.232.219.223'
// PEM content
        PemContent = '''-----BEGIN RSA PRIVATE KEY-----
MIIEpgIBAAKCAQEAu+SemHmGLsQbYpqP6iJ9Rd8jgzgzwCPwzE97XGSyYQLPHj7n
iatWbWFxu0I955AHC54kOVTG9UGppVWzcyhw/N60GOhqWs26eFafMqp19o0L4nbj
bfv+ck9B07lr10zmznwPP62qiOrfSOv9vmCX7Xc+q1K2gsv4odFpd5+P2YMvndKv
uo5wdmmsCi/nNasZGv5wlgW81/13/HphIkLYhlCKQw4dMr6N6ZMDInKdsvkZqqdE
fL/Vm5g1fT862Je9WW1TxJU47mTvw0OHeqemkOQmmcfJByqQu85WRABnhZWtTuGG
sQyvd4GLpzIBPs7/uGkist/TF/+mRPfCybnw8wIDAQABAoIBAQCvAwAGngsyPFAV
xEoQmBd/oZx+eTca3V9Jry8EnHbajbdGQOmB/in+sCkzdzwaGLm3RJIRJLo9b/AI
pI8F0MGNiBLQsIuAEOiDdovKsaQ3BqHvSzYEGBileNWj1K5yWJsT8dJySdzys3NP
hSFQ1sAs3ElvYxfFNSBd+aT3W5St0DtQ8lGC/Uwm5q+DqnSBJw6cG2P873CermJG
8wrCVZiubVhYg92zAfFck2tD3L9deup05+GRjx34VBeEQi1RvHz0BFZdBu0OyrWR
IJnUBJyuVGwGRf30/Qj/UeH/+o9eMaKoehqnC09sKeHrhCHBnLhDPdbQJZZHxrMY
6TLXnXEBAoGBAPxG8VGW0tjxpm9VAF2IP2/u9u0Sj1THtCJVE5dukR2HDE7SVhZ2
TbnUuIWKQStpt9J/vJnQJ12IR7jLk1XUiBeIeayzulWT9KairXetD4F3Bd2X/b5F
ETG8fdqefsz11LiHE8lTfzoP5alsGHgWDsSmlAgyO6AmBsXvED9iu3+hAoGBAL6q
cgo6A8K2fOyDsRsEDCEhydDPdGdd8O1tNEnjmf+OOsJz57l55Yu87v10dnpYcYF6
QGi1r0SYkIz2z13cnbO8t0l08dxeJZYGChSdaCqGE+vLg6u7upocNIwlJKyXXYL8
/koidqsJMeidvmBScVB1KfVjawU2H7+K4LzAfHgTAoGBAMYsjIEFM9iL66xlX4YZ
84tgcsTE4ac9wXl+z4cTqhdB8R8yB8JZaLFkMxdkBdGumGJZSbSiWWoWFhFKihDQ
hZ/G654JCYUUVwlXnWD3p8iR0R/Dt+ZxpxIk+dB01TL4ogPayGZMmxLnTggLDCW5
XhyIppzRqRkjBgsSwvan/pZBAoGBAIZaixOjv+SkPTFYtyMnXfi224SVuxvfuZdE
3ZS3mlvXeJZvoDk1qUgnsWzOdXJPAmTPpbVjWYcHzsxWWtd0T3hJIgrUKl4zxV6L
EO1n4fAdHMqKVVVdauePuoOK5OXtn4pfBj1tPZvWVeHpzi4Hyit89D0jelVox0Rs
82JubQcbAoGBAKhHM2SVQRL1zzmNvjdD1QxZpKrHcO4PnX/WvrefJky9GAXgdsxX
iE3TS+hMB3VXe4uT+T13wtuu8Z9qXFtsAuAU5/tZnfFvHgXevvqCLrocJUoD6aEr
oeRbgQUOgPQvSAjSg4aizfnZ+MO4TUdh94PY/jf++78GlQ/Km26eZDnq
-----END RSA PRIVATE KEY-----'''

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
                    sshagent([['keyfile': "${PemContent}"]]) {
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

