package io.advantageous.gradle;

import java.util.ArrayList;
import java.util.List;

public class DockerFile {

  private final String fileLocation;
  private String baseImage;
  private String cmd;
  private List<Integer> exposedPorts = new ArrayList<>(2);
  private List<String> addCommands = new ArrayList<>(2);

  public DockerFile(final String fileLocation) {
    this.fileLocation = fileLocation;
  }

  public DockerFile() {
    this.fileLocation = null;
  }

  public void exposePort(final Integer port) {
    this.exposedPorts.add(port);
  }

  public void add(final String source, final String destination) {
    this.addCommands.add(source + " " + destination);
  }

  public String getFileLocation() {
    return fileLocation;
  }

  public String getBaseImage() {
    return baseImage;
  }

  public void setBaseImage(String baseImage) {
    this.baseImage = baseImage;
  }

  public String getCmd() {
    return cmd;
  }

  public void setCmd(String cmd) {
    this.cmd = cmd;
  }

  public List<Integer> getExposedPorts() {
    return exposedPorts;
  }

  public List<String> getAddCommands() {
    return addCommands;
  }
}
