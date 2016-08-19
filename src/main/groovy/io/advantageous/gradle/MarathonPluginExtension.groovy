package io.advantageous.gradle

import org.gradle.api.NamedDomainObjectContainer

class MarathonPluginExtension {
    String dockerRegistry
    NamedDomainObjectContainer<MarathonEnvironment> environments

    def environments(Closure configureClosure) {
        environments.configure(configureClosure)
    }
}
