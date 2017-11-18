package io.advantageous.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class DockerContainerPlugin implements Plugin<Project> {

    void apply(Project project) {

        project.extensions.create('docker', DockerPluginExtension)
        project.docker.dockerFile = project.container(DockerFile)

        project.task("dockerBuild",
                dependsOn: "build",
                description: "Build a docker container.") << {

        }

        project.task("dockerPush",
                dependsOn: "dockerBuild",
                description: "Push container to the docker repository.") << {

        }

    }
}
