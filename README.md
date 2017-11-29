# Marathon Deploy Plugin for Gradle

This plugin is used to deploy an application to [Marathon](https://mesosphere.github.io/marathon/).
Version 3 supports Kotlin DSL, automatic docker config and property overrides per environment.
## Usage

### build.gradle.kts

    plugins {
      id("io.advantageous.marathon") version "3.0.10"
    }
    
    marathon {
    
      val dockerTag = "myrepo-docker.jfrog.io/my-service:${project.version}"
    
      // This defines the docker container.
      docker {
        tag(dockerTag)
        if (gitBranch() == "master") {
          tag("rbss-docker.jfrog.io/event-service:lates1d dddt")
        }
        dockerFile {
          baseImage = "openjdk:8-jre-alpine"
          add("libs/my-service-${project.version}.jar", "my-service.jar")
          exposePort(8080)
          cmd = "java -Xmx2g -Xmx2g -jar /my-service.jar"
        }
      }
    
      // This is an optional way of overriding keys in the marathon.json file.
      application(mapOf(
          "mem" to 2048,
          "container.docker.image" to dockerTag
      ))
    
      environments {
        "integration" {
          marathonApi("http://url-for-for-your-integration-marathon-instance:8080")
        }
        "staging" {
          marathonApi("http://url-for-for-your-staging-marathon-instance:8080")
          application(mapOf(
              "env.SPRING_PROFILES_ACTIVE" to "staging"
          ))
        }
        if (!project.version.toString().contains("SNAPSHOT")) "production" {
          marathonApi("http://url-for-for-your-production-marathon-instance:8080")
          application(mapOf(
              "env.SPRING_PROFILES_ACTIVE" to "production",
              "cpus" to 1,
              "instances" to 3
          ))
        }
      }
    }
    
### Marathon JSON Files

The plugin can take a Marathon application json template or your can construct it with the application builder.
If there is a *marathon.json* in the root of the project, it will be used.  Environment specific json files can override
the default by specifying the env name in the file like *marathon-staging.json*.

These files are exactly what you would put in a regular marathon json configuration, with a couple extra conveniences.

#### marathon-staging.json
    {
        "id": "${project.name}",
        "instances": 1,
        "cpus": 1.0,
        "mem": 512,
        "container": {
          "type": "DOCKER",
          "image": "${project.docker.name}",
          "docker": {
            "network": "HOST",
            "forcePullImage": true,
            "portDefinitions": [
              {
                "port": 0,
                "protocol": "tcp",
                "name": "eventbus"
              },
              {
                "port": 0,
                "protocol": "tcp",
                "name": "admin"
              }
            ]
          }
        },
        "healthChecks": [
            {
                "protocol": "HTTP",
                "intervalSeconds": 20,
                "maxConsecutiveFailures": 3,
                "gracePeriodSeconds": 30,
                "portIndex": 1,
                "path": "/__admin/ok"
            }
        ],
        "upgradeStrategy": {
            "minimumHealthCapacity": 0
        }
    }
    
You will notice variable substitutions for the docker image name and the app id.  The file is parsed by gradle before it is used so you can use anything in the project metadata.
    
### Gradle Commands
    
    $ gradle dockerFile #Build a dockerfile
    
    $ gradle dockerBuild #Build a docker container
    
    $ gradle showMarathonEnvironments
    
    $ gradle deployMyEnvName
       
You can show what environments are registered with the **showMarathonEnvironments** command.
A dynamic task is generated for each environment you setup in your build.gradle file.  In the example above, you would be able to run the following commands:
    
    $ gradle deployStaging
    
    $ gradle deployProduction
        
You can always list all possible tasks with:

    $ gradle tasks
    
#### Dry Run

If you want to see the json created by the plugin without actually deploying it to your env.  You can pass a flag to do a dry run.  This will just print out the generated json.

    $ gradle deployStaging -PdryRun
    
You can also check the marathon deploy file parsing by with the command:

    $ gradle interpretStagingConfig
