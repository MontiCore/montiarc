/* (c) https://github.com/MontiCore/monticore */
package montiarc.build

import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication

class Publish {
  companion object {
    fun MavenPublication.configurePublication(project: Project) {
      artifactId = project.name
      pom.url.set("https://github.com/MontiCore/montiarc")
      pom.licenses {
        this.license {
          this.name.set("MontiCore-3-Level-License-Model")
          this.url.set("https://monticore.github.io/monticore/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL/")
        }
      }
      pom.developers {
        this.developer {
          this.name.set("The MontiArc Team")
          this.organization.set("Software Engineering RWTH Aachen University")
          this.organizationUrl.set("https://monticore.de/")
        }
      }
    }
  }
}
