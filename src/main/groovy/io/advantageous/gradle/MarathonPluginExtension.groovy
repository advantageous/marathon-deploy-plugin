package io.advantageous.gradle

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer

class MarathonPluginExtension {

    NamedDomainObjectContainer<MarathonEnvironment> environments

    Map<String, Object> application

    DockerContainer dockerContainer

    def environments(Closure configureClosure) {
        environments.configure(configureClosure)
    }

    def application(Map<String, Object> application) {
        this.application = application
    }

    def docker(Action<DockerContainer> action) {
        dockerContainer.apply(action)
    }
}
