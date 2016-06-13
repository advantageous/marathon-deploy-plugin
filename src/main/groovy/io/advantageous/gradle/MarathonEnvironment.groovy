package io.advantageous.gradle

import groovy.json.JsonBuilder

class MarathonEnvironment {

    private final String name

    private String marathonApi;
    private String jsonLocation;
    private String mavenRepo;

    MarathonEnvironment(String name) {
        this.name = name
    }

    MarathonEnvironment marathonApi(String marathonApi) {
        this.marathonApi = marathonApi
        this
    }

    MarathonEnvironment mavenRepo(String mavenRepo) {
        this.mavenRepo = mavenRepo
        this
    }

    MarathonEnvironment jsonLocation(String jsonLocation) {
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
        return jsonLocation ?: "marathon/${name}.json"
    }

    String toString() {
        new JsonBuilder(this).toPrettyString()
    }
}
