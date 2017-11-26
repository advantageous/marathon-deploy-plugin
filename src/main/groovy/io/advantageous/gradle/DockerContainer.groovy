package io.advantageous.gradle

import org.gradle.api.Project

class DockerContainer {

    final Project project

    DockerContainer(Project project) {
        this.project = project
    }

    List<String> dockerTags = []
    DockerFile dockerFile

    def dockerFile(Closure config) {
        this.dockerFile = new DockerFile(this.project)
        this.project.configure(this.dockerFile, config)
    }

    def tag(String tag) {
        dockerTags.add(tag)
    }

}
