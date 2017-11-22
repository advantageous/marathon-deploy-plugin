package io.advantageous.gradle

import groovy.json.JsonBuilder

class MarathonEnvironment {

    private final String name

    private String marathonApi
    private String jsonLocation
    private Map<String, Object> application

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

    MarathonEnvironment application(Map<String, Object> application) {
        this.application = application
        this
    }

    String getName() {
        return name
    }

    String getMarathonApi() {
        return marathonApi
    }

    Map<String, Object> getApplication() {
        return application
    }

    String getJsonLocation() {
        if (jsonLocation) {
            return jsonLocation
        } else if (new File("marathon.${name}.json").exists()) {
            return "marathon.${name}.json"
        } else if (new File("marathon.json").exists()) {
            return "marathon.json"
        } else {
            return null
        }
    }

    String toString() {
        new JsonBuilder(this).toPrettyString()
    }
}
