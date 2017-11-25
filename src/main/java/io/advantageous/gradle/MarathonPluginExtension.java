package io.advantageous.gradle;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;

import java.util.Map;

public class MarathonPluginExtension {

  private final Project project;

  private NamedDomainObjectContainer<MarathonEnvironment> environments;
  private Map<String, Object> application;
  private DockerContainer dockerContainer;

  public MarathonPluginExtension(final Project project) {
    this.project = project;
  }

  public void environments(final Closure configureClosure) {
    environments.configure(configureClosure);
  }

  public void application(final Map<String, Object> application) {
    this.application = application;
  }

  public void docker(final Action<DockerContainer> action) {
    this.dockerContainer = new DockerContainer(this.project);
    action.execute(this.dockerContainer);
  }

  public NamedDomainObjectContainer<MarathonEnvironment> getEnvironments() {
    return environments;
  }

  public Map<String, Object> getApplication() {
    return application;
  }

  public DockerContainer getDockerContainer() {
    return dockerContainer;
  }
}
