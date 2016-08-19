package io.advantageous.gradle

import groovy.json.JsonBuilder

class MarathonEnvironment {

    private final String name

    private String marathonApi;
    private String jsonLocation;

    MarathonEnvironment(String name) {
        this.name = name
    }

    MarathonEnvironment marathonApi(String marathonApi) {
        this.marathonApi = marathonApi
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

    String getJsonLocation() {
        return jsonLocation ?: "marathon/${name}.json"
    }

    String toString() {
        new JsonBuilder(this).toPrettyString()
    }
}
