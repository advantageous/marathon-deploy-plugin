package io.advantageous.gradle

import org.slf4j.LoggerFactory

class DockerUtils {
    static logger = LoggerFactory.getLogger(DockerUtils.class.getName())

    static runDocker(String dockerCoordinates) {
        logger.info('docker run -P ' + dockerCoordinates)
        runCommand("docker", "run", "-P", dockerCoordinates)
    }

    static pushDocker(String dockerCoordinates) {
        logger.info("docker push ${dockerCoordinates}")
        runCommand("docker", "push", dockerCoordinates)
    }

    static buildDocker(String dockerCoordinates) {
        logger.info("docker build -t ${dockerCoordinates} build/")
        runCommand("docker", "build", "-t", dockerCoordinates, "build/")
    }

    static runCommand(String command) {

        logger.infoZ("Running command $command")

        String[] args = ["/bin/sh", "-c", "$command"]

        def stringBuilder = new StringBuilder()
        def processBuilder = new ProcessBuilder()
                .command(args)
                .redirectErrorStream(true)
        def process = processBuilder.start()
        process.waitFor()

        process.inputStream.eachLine {
            println it
            stringBuilder.append(it)
        }

        println(stringBuilder.toString())
        [process.exitValue(), stringBuilder.toString()]
    }
}
