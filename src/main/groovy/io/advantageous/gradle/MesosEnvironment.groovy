package io.advantageous.gradle

import groovy.json.JsonBuilder

class MesosEnvironment {

    private final String name

    private String marathonApi;
    private String jsonLocation;
    private String mavenRepo;

    MesosEnvironment(String name) {
        this.name = name
    }

    MesosEnvironment marathonApi(String marathonApi) {
        this.marathonApi = marathonApi
        this
    }

    MesosEnvironment mavenRepo(String mavenRepo) {
        this.mavenRepo = mavenRepo
        this
    }

    MesosEnvironment jsonLocation(String jsonLocation) {
        this.jsonLocation = jsonLocation
        this
    }

    String getName() {
        return name
    }

    String getMarathonApi() {
        return marathonApi
    }

    String getMavenRepo() {
        return mavenRepo
    }

    String getJsonLocation() {
        return jsonLocation ?: "mesos/${name}.json"
    }

    String toString() {
        new JsonBuilder(this).toPrettyString()
    }
}
