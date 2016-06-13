# Marathon Deploy Plugin for Gradle

This plugin is used in conjunction with the [application plugin](https://docs.gradle.org/current/userguide/application_plugin.html) to deploy an application to [Marathon](https://mesosphere.github.io/marathon/).

## Usage

### build.gradle

	buildscript {
		repositories {
            mavenCentral()
		}
		dependencies {
            classpath 'io.advantageous.gradle:marathon-deploy-plugin:1.0.0'
		}
	}

	apply plugin: 'application'
	apply plugin: 'marathon-deploy'
    
    marathon {
    	url = "http://path-to-your-marathon-instance.com"
    }
    
    mesosEnvironments {
        staging {
            marathonApi "http://url-for-for-your-staging-marathon-instance:8080"
            mavenRepo "http://url-for-for-your-artifactory-instance:8081/artifactory/libs-snapshot-local/"
        }
        production {
            marathonApi "http://url-for-for-your-production-marathon-instance:8080"
            mavenRepo "http://url-for-for-your-artifactory-instance:8081/artifactory/libs-release-local/"
        }
    }
    
### Marathon JSON Files

By default the plugin expects a **marathon** directory in the root of your project where there will be a json file for each of your configured environments.
With the above example, you would have a *staging.json* and a *production.json*.

    myProject
      + marathon
        - staging.json
        - production.json
      - build.gradle

These files are exactly what you would put in a regular marathon json configuration, with a couple extra conveniences.

#### staging.json
    {
        "instances": 1,
        "cpus": 1.0,
        "mem": 512,
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
        ],
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
    
You will notice a few common fields are missing.  The **id**, **cmd** and **uris** fields are automatically created from the layout of your project when the plugin runs.
If you want to use your own values, you may set any of those fields and yours will take precedence.

#### Extra URIs

If you have additional dependencies for your application that you want to list in the *uris* field, but want the convenience of an auto-generated URI from your maven repository, you can put then in a *additionalUris* field and they will be merged.
    
    {
        "instances": 1,
        "cpus": 1.0,
        "mem": 512,
        "additionalUris": [
            "http://path-to-some-other-dependency"
        ],
        "portDefinitions": [
            {
                "port": 0,
                "protocol": "tcp",
            },
        ]
    }
    