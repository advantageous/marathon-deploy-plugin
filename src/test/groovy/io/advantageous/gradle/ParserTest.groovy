package io.advantageous.gradle

import groovy.json.JsonSlurper
import org.junit.Assert
import org.junit.Test

class ParserTest {

    static slurper = new JsonSlurper()

    @Test
    void testParser() {
        def config = slurper.parse(new File(getClass().getResource('/marathon.json').toURI()))
        MarathonDeployPlugin.parseStrings(config, [version: "1.2.3"])
        def evaluated = MarathonDeployPlugin.evalProps(config, [
                "id"                        : "/new/value",
                "env.SPRING_PROFILES_ACTIVE": "production",
                "cpus"                      : 1,
                "instances"                 : 3
        ])
        println evaluated
        Assert.assertNotNull(evaluated)
        Assert.assertEquals("/new/value", evaluated.id)
        Assert.assertEquals(3, evaluated.instances)
        Assert.assertEquals("DOCKER", evaluated.container.type)
    }
}
