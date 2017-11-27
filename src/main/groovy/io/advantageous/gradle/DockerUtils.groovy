package io.advantageous.gradle

import org.slf4j.LoggerFactory

class DockerUtils {
    static logger = LoggerFactory.getLogger(DockerUtils.class.getName())

    static runDocker(String dockerCoordinates) {
        def cmd = "docker run -P $dockerCoordinates"
        logger.info(cmd)
        runCommand(cmd)
    }

    static pushDocker(String dockerCoordinates) {
        def cmd = "docker push $dockerCoordinates"
        logger.info(cmd)
        runCommand(cmd)
    }

    static buildDocker(List<String> tags) {
        def cmd = "docker build"
        tags.each { cmd += " -t $it" }
        cmd += " build/"
        logger.info(cmd)
        runCommand(cmd)
    }

    static runCommand(String command) {

        logger.info("Running command $command")
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

        if (process.exitValue() != 0) throw new RuntimeException("Command failed: $command")

        def result = stringBuilder.toString()
        println(result)
        return result
    }
}
