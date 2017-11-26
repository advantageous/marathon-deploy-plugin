package io.advantageous.gradle

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

class MarathonPluginExtension {

    final Project project

    MarathonPluginExtension(Project project) {
        this.project = project
    }

    NamedDomainObjectContainer<MarathonEnvironment> environments

    Map<String, Object> application

    DockerContainer docker

    def environments(Closure configureClosure) {
        environments.configure(configureClosure)
    }

    def application(Map<String, Object> application) {
        this.application = application
    }

    def docker(Action<DockerContainer> configureClosure) {
        this.docker = new DockerContainer(this.project)
        this.project.configure([this.docker], configureClosure)
    }
}
