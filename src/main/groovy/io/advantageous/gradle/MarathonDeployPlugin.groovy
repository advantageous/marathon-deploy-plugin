package io.advantageous.gradle

import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine
import groovyx.net.http.RESTClient
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory

import static groovy.json.JsonOutput.prettyPrint
import static groovy.json.JsonOutput.toJson

class MarathonDeployPlugin implements Plugin<Project> {

    static logger = LoggerFactory.getLogger(MarathonDeployPlugin.class.getName())

    void apply(Project project) {

        project.extensions.create('marathon', MarathonPluginExtension, project)
        project.marathon.environments = project.container(MarathonEnvironment)

        project.task("dockerFile") {
            doLast {
                DockerFile dockerFile = project.marathon.docker.dockerFile
                String generatedFile
                if (dockerFile.fileLocation) {
                    generatedFile = project.file(dockerFile.fileLocation).toString()
                } else {
                    generatedFile = "FROM ${dockerFile.baseImage}\n"
                    dockerFile.addCommands.each { generatedFile += "ADD ${it}\n" }
                    dockerFile.exposedPorts.each { generatedFile += "EXPOSE ${it}\n" }
                    generatedFile += "CMD ${dockerFile.cmd}\n"
                }
                def file = project.file("build/Dockerfile")
                if (file.exists()) file.delete()
                file << generatedFile
            }
        }

        project.task("dockerBuild", dependsOn: "dockerFile") {
            doLast {
                DockerUtils.buildDocker(project.marathon.docker.dockerTags as List<String>)
            }
        }

        project.task("dockerRun") {
            doLast {
                DockerUtils.runDocker((project.marathon.docker as DockerContainer).dockerTags.first())
            }
        }

        project.task("dockerPush", dependsOn: "dockerBuild") {
            doLast {
                (project.marathon.docker as DockerContainer).dockerTags.each { DockerUtils.pushDocker(it) }
            }
        }

        project.task("showMarathonEnvironments") {
            doLast {
                project.marathon.environments.forEach { println it }
            }
        }

        project.afterEvaluate {

            project.marathon.environments.each { thisEnv ->

                def capitalizedName = thisEnv.getName().capitalize()
                project.task("interpret${capitalizedName}Config",
                        description: "Parse the configuration file for the ${thisEnv.getName()} environment") {
                    doLast {
                        def marathonConfig = getMarathonConfig(thisEnv, project)
                        logger.debug("marathon config: ${marathonConfig.toString()}")
                        println prettyPrint(toJson(marathonConfig))
                    }
                }

                project.task("deploy${capitalizedName}",
                        dependsOn: "dockerPush",
                        description: "Deploy to Marathon in the ${thisEnv.getName()} environment") {
                    doLast {
                        def marathonConfig = getMarathonConfig(thisEnv, project)
                        if (!marathonConfig.env) {
                            marathonConfig.env = [:]
                        }
                        marathonConfig.env.DEPLOYMENT_ENVIRONMENT =
                                marathonConfig.env.DEPLOYMENT_ENVIRONMENT ?: thisEnv.getName()
                        if (project.hasProperty("dryRun")) {
                            println "DRY RUN JSON:"
                            println prettyPrint(toJson(marathonConfig))
                        } else {
                            def client = new RESTClient(thisEnv.marathonApi, "application/json")
                            client.ignoreSSLIssues()

                            def listResp = client.get(path: "/v2/apps")
                            if (listResp.status == 200) {
                                def data = listResp.getData()
                                logger.debug "got list of apps in marathon:"
                                logger.debug toJson(data)
                                def found = data.apps.find { it.id == "/${marathonConfig.id}" }
                                if (found) {
                                    logger.info "Found ${marathonConfig.id} in Marathon. Going to do a PUT"
                                    def putResp = client.put(
                                            path: "/v2/apps/${marathonConfig.id}",
                                            body: toJson(marathonConfig)
                                    )
                                    logger.info "put response:"
                                    logger.info toJson(putResp.getData())
                                } else {
                                    logger.info "Did not find ${marathonConfig.id} in Marathon. Going to do a POST"
                                    def postResp = client.post(
                                            path: "/v2/apps/",
                                            body: toJson(marathonConfig)
                                    )
                                    logger.info "post response:"
                                    logger.info toJson(postResp.getData())
                                }
                            } else {
                                throw new IllegalStateException(
                                        "Could not query $thisEnv.marathonApi for apps. " +
                                                "Failed with code: $listResp.status"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    static slurper = new JsonSlurper()

    static evalProps(config, props) {
        props.each { String stringPath, newValue ->
            Eval.xy(config, newValue, 'x.' + stringPath + '= y')
        }
        return config
    }

    static parseStrings(target, project) {
        for (entry in target) {
            logger.debug("parsing ${entry}")
            if (entry.getValue() instanceof Map) {
                parseStrings(entry.getValue(), project)
            } else if (entry.getValue() instanceof List) {
                logger.debug("collection: ${entry.getValue()}")

                def theList = entry.getValue() as List
                for (int i = 0; i < theList.size(); i++) {
                    def it = theList[i]
                    if (it instanceof String) {
                        theList[i] = new SimpleTemplateEngine()
                                .createTemplate(it as String)
                                .make([project: project])
                                .toString()
                    } else {
                        parseStrings(it, project)
                    }
                }
            } else if (entry.getValue() instanceof String) {
                def parsed = new SimpleTemplateEngine()
                        .createTemplate(entry.getValue() as String)
                        .make([project: project])
                        .toString()
                entry.setValue(parsed)
            }
        }
    }

    static getMarathonConfig(thisEnv, Project project) {
        def config
        if (thisEnv.jsonLocation) {
            config = slurper.parse(project.file(thisEnv.jsonLocation))
        } else {
            config = [:]
        }
        parseStrings(config, project)
        def allEnvironments = evalProps(config, project.marathon.application)
        def evaluated = evalProps(allEnvironments, thisEnv.application)
        return evaluated
    }

}
