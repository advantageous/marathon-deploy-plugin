package io.advantageous.gradle;

import groovy.lang.Closure;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;

public class DockerContainer {

  private final Project project;

  private List<String> dockerTags = new ArrayList<>();
  private DockerFile dockerFile;

  public DockerContainer(final Project project) {
    this.project = project;
  }

  public List<String> getDockerTags() {
    return dockerTags;
  }

  public DockerFile getDockerFile() {
    return dockerFile;
  }

  public void setDockerFile(DockerFile dockerFile) {
    this.dockerFile = dockerFile;
  }

  void tag(String dockerTag) {
    this.dockerTags.add(dockerTag);
  }

  void dockerFile(Closure config) {
    dockerFile = new DockerFile();
//    project.configure(Collections.singletonList(dockerFile), action);
    project.configure(dockerFile, config);
  }

//    def dockerFile(String fileLocation) {
//        this.dockerFile = new DockerFileExtension(fileLocation)
//        return dockerFile
//    }
}
