package io.advantageous.gradle

import groovy.json.JsonSlurper
import groovyx.net.http.RESTClient
import org.gradle.api.Plugin
import org.gradle.api.Project

import static groovy.json.JsonOutput.prettyPrint
import static groovy.json.JsonOutput.toJson

public class MarathonDeployPlugin implements Plugin<Project> {

    void apply(Project project) {

        project.extensions.mesosEnvironments = project.container(MesosEnvironment)

        project.task("showMesosEnvironments") << {
            project.extensions.mesosEnvironments.forEach { println it }
        }

        project.afterEvaluate {
            def slurper = new JsonSlurper()
            project.extensions.mesosEnvironments.each { MesosEnvironment myEnv ->
                project.task("deploy${myEnv.name.capitalize()}",
                        description: "Deploy to Mesos in the ${myEnv.name.capitalize()} environment") << {

                    def mesosConfig = slurper.parse(project.file(myEnv.jsonLocation))
                    mesosConfig.id = mesosConfig.id ?: project.name
                    mesosConfig.cmd = mesosConfig.cmd ?: "${project.name}-${project.version}/bin/${project.name}"
                    mesosConfig.uris = mesosConfig.uris ?: [
                            myEnv.mavenRepo +
                                    project.group.replaceAll('\\.', '/') + '/' +
                                    project.name + '/' + project.version + '/' +
                                    project.name + '-' + project.version + '.zip'
                    ]
                    if (mesosConfig.additionalUris) {
                        mesosConfig.uris += mesosConfig.additionalUris
                        mesosConfig.additionalUris = null;
                    }
                    if (!mesosConfig.env) {
                        mesosConfig.env = [:]
                    }
                    mesosConfig.env.DEPLOYMENT_ENVIRONMENT = mesosConfig.env.DEPLOYMENT_ENVIRONMENT ?: myEnv.name

                    if (project.hasProperty("dryRun")) {
                        println "DRY RUN JSON:"
                        println prettyPrint(toJson(mesosConfig))
                    } else {
                        def client = new RESTClient(myEnv.marathonApi, "application/json")
                        client.ignoreSSLIssues()

                        def listResp = client.get(path: "/v2/apps")
                        if (listResp.status == 200) {
                            def data = listResp.getData()
                            logger.debug "got list of apps in marathon:"
                            logger.debug toJson(data)
                            def found = data.apps.find { it.id == "/$project.name" }
                            if (found) {
                                logger.info "Found $project.name in Marathon. Going to do a PUT"
                                def putResp = client.put(path: "/v2/apps/$project.name", body: toJson(mesosConfig))
                                logger.info "put response:"
                                logger.info toJson(putResp.getData())
                            } else {
                                logger.info "Did not find $project.name in Marathon. Going to do a POST"
                                def postResp = client.post(path: "/v2/apps/", body: toJson(mesosConfig))
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
