package io.advantageous.gradle

import org.gradle.api.Project

class DockerFile {

    final Project project

    DockerFile(Project project) {
        this.project = project
    }

    String fileLocation
    String baseImage
    String cmd
    List<Integer> exposedPorts = []
    List<String> addCommands = []

    def exposePort(final Integer port) {
        this.exposedPorts.add(port)
    }

    def add(final String source, final String destination) {
        this.addCommands.add(source + " " + destination)
    }

}
