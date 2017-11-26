package io.advantageous.gradle

import org.gradle.internal.impldep.org.junit.Rule
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FunctionalTests {

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    private File buildFile

    @Before
    void setup() throws IOException {
        testProjectDir.create()
        buildFile = testProjectDir.newFile("build.gradle")
    }

    @Test
    void testDockerFile() throws IOException {
        String buildFileContent = """plugins {id "io.advantageous.marathon"}
marathon {
  docker {

    tag "rbss-docker.jfrog.io/event-service:123"
    tag "rbss-docker.jfrog.io/event-service:latest"
    
    dockerFile {
      baseImage = "openjdk:8-jre-alpine"
      add("libs/event-service-123.jar", "event-service.jar")
      exposePort(8080)
      cmd = "java -Xmx2g -Xmx2g -jar /event-service.jar"
    }
  }
}
"""
        buildFile << buildFileContent

        BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("dockerFile")
                .withPluginClasspath()
                .build()

        Assert.assertEquals(result.task(":dockerFile").getOutcome(), TaskOutcome.SUCCESS)
    }
}
