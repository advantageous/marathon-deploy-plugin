package io.advantageous.gradle

import org.gradle.api.Action
import org.gradle.api.Project

class DockerContainer {

    final Project project

    DockerContainer(Project project) {
        this.project = project
    }

    List<String> dockerTags = []
    DockerFile dockerFile

    def dockerFile(Action<DockerFile> config) {
        this.dockerFile = new DockerFile(this.project)
        this.project.configure([this.dockerFile], config)
    }

    def tag(String tag) {
        dockerTags.add(tag)
    }

}
