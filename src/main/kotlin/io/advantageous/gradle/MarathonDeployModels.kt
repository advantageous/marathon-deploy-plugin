package io.advantageous.gradle

import org.gradle.api.NamedDomainObjectContainer
import java.io.File

//class MarathonPluginExtension {
//
//  val environments : NamedDomainObjectContainer<MarathonEnvironment>
//
//  fun environments(configureClosure: Unit) {
//    environments.configure(configureClosure)
//  }
//}

class MarathonEnvironment(val name: String) {

  var marathonApi: String? = null

  private var jsonLocation: String? = null

  var props: Map<String, Any>? = null

  fun marathonApi(marathonApi: String): MarathonEnvironment {
    this.marathonApi = marathonApi
    return this
  }

  fun jsonLocation(jsonLocation: String): MarathonEnvironment {
    this.jsonLocation = jsonLocation
    return this
  }

  fun props(props: Map<String, Any>): MarathonEnvironment {
    this.props = props
    return this
  }

  fun getJsonLocation(): String = when {
    jsonLocation != null -> jsonLocation!!
    File("marathon/\${name}.json").exists() -> "marathon/\${name}.json"
    else -> "marathon.json"
  }

}