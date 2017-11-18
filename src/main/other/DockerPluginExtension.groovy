package io.advantageous.gradle

import org.gradle.api.NamedDomainObjectContainer

class DockerPluginExtension {

    String dockerTag
    String dockerFileLocation
    NamedDomainObjectContainer<DockerFile> dockerFile

    def dockerFile(Closure configureClosure) {
        dockerFile.configure(configureClosure)
    }
}
