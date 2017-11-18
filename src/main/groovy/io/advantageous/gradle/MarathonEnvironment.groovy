package io.advantageous.gradle

import groovy.json.JsonBuilder

class MarathonEnvironment {

    private final String name

    private String marathonApi
    private String jsonLocation
    private Map<String, Object> props

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

    MarathonEnvironment props(Map<String, Object> jsonLocation) {
        this.jsonLocation = jsonLocation
        this
    }

    String getName() {
        return name
    }

    String getMarathonApi() {
        return marathonApi
    }

    Map<String, Object> getProps() {
        return props
    }

    String getJsonLocation() {
        return jsonLocation ?: new File("marathon/${name}.json").exists() ? "marathon/${name}.json" : "marathon.json"
    }

    String toString() {
        new JsonBuilder(this).toPrettyString()
    }
}
