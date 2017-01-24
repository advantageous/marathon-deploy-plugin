# Marathon Deploy Plugin for Gradle

This plugin is used to deploy an application to [Marathon](https://mesosphere.github.io/marathon/).

## Usage

### build.gradle

    plugins {
      id "io.advantageous.marathon" version "2.0.0"
    }
    
    marathon.environments {
        staging {
            marathonApi "http://url-for-for-your-staging-marathon-instance:8080"
        }
        production {
            marathonApi "http://url-for-for-your-production-marathon-instance:8080"
        }
    }
    
### Marathon JSON Files

By default the plugin expects a **marathon** directory in the root of your project where there will be a json file for each of your configured environments.
With the above example, you would have a *staging.json* and a *production.json*.  The name of the json files **must** match the name of the marathon environment declared in the build.gradle.

    myProject
      + marathon
        - staging.json
        - production.json
      - build.gradle

These files are exactly what you would put in a regular marathon json configuration, with a couple extra conveniences.

#### staging.json
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
