buildscript {
  repositories {
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
  }
  dependencies {
    classpath("com.gradle.publish:plugin-publish-plugin:0.9.4")
  }
}

plugins {
  `java-gradle-plugin`
  `kotlin-dsl`
  `maven-publish`
}

apply {
  plugin("com.gradle.plugin-publish")
}

group = "io.advantageous.gradle"
version = "2.5.0"

repositories {
  jcenter()
}

dependencies {
  compile(gradleApi())
}

gradlePlugin {
  (plugins) {
    "marathon-deploy-plugin" {
      id = "io.advantageous.marathon"
      implementationClass = "io.advantageous.gradle.MarathonDeployPlugin"
    }
  }
}

publishing {
  repositories {
    maven(url = "build/repository")
  }
}


//pluginBundle {
//  website = "https://github.com/advantageous/marathon-deploy-plugin"
//  vcsUrl = "https://github.com/advantageous/marathon-deploy-plugin.git"
//  description = "Deploy your application to an Apache Marathon cluster."
//  tags = arrayOf("marathon", "mesos", "deploy", "continuous delivery")

//  plugins {
//    marathonDeployPlugin {
//      id = "io.advantageous.marathon"
//      displayName = "Marathon Deployment Plugin"
//    }
//  }
//}