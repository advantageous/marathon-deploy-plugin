package io.advantageous.gradle

import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine
import groovyx.net.http.RESTClient
import org.gradle.api.Plugin
import org.gradle.api.Project

import static groovy.json.JsonOutput.prettyPrint
import static groovy.json.JsonOutput.toJson

public class MarathonDeployPlugin implements Plugin<Project> {

    static parseStrings(target, project) {
        for (entry in target) {
            if (entry.getValue() instanceof Map) {
                parseStrings(entry.getValue(), project)
            } else if (entry.getValue() instanceof Collection) {
                ((Collection) entry.getValue()).forEach { parseStrings(it, project) }
            } else if (entry.getValue() instanceof String) {
                def parsed = new SimpleTemplateEngine()
                        .createTemplate(entry.getValue() as String)
                        .make([project: project])
                        .toString()
                entry.setValue(parsed)
            }
        }
    }

    void apply(Project project) {

        project.extensions.create('marathon', MarathonPluginExtension)
        project.marathon.environments = project.container(MarathonEnvironment)

        project.task("showMarathonEnvironments") << {
            project.marathon.environments.forEach { println it }
        }

        project.afterEvaluate {
            def slurper = new JsonSlurper()

            project.marathon.environments.each { thisEnv ->

                def capitalizedName = thisEnv.getName().capitalize()
                project.task("interpret${capitalizedName}Config",
                        description: "Parse the configuration file for the ${thisEnv.getName()} environment") << {
                    def config = slurper.parse(project.file(thisEnv.jsonLocation))
                    parseStrings(config, project)
                    println prettyPrint(toJson(config))
                }

                project.task("deploy${capitalizedName}",
                        dependsOn: "dockerPush",
                        description: "Deploy to Marathon in the ${thisEnv.getName()} environment") << {
                    def marathonConfig = slurper.parse(project.file(thisEnv.jsonLocation))
                    parseStrings(marathonConfig, project)
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
                            def found = data.apps.find { it.id == "/$project.name" }
                            if (found) {
                                logger.info "Found $project.name in Marathon. Going to do a PUT"
                                def putResp = client.put(path: "/v2/apps/$project.name", body: toJson(marathonConfig))
                                logger.info "put response:"
                                logger.info toJson(putResp.getData())
                            } else {
                                logger.info "Did not find $project.name in Marathon. Going to do a POST"
                                def postResp = client.post(path: "/v2/apps/", body: toJson(marathonConfig))
                                logger.info "post response:"
                                logger.info toJson(postResp.getData())
                            }
                        } else {
                            throw new IllegalStateException(
                                    "Could not query $myEnv.marathonApi for apps. Failed with code: $listResp.status"
                            )
                        }
                    }
                }
            }
        }

    }

}
