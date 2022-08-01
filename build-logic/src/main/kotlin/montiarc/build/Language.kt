/* (c) https://github.com/MontiCore/monticore */
package montiarc.build

import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByName

class Language {
  companion object{
    fun Project.configureMCTask(grammarName: String){
      tasks.getByName<de.monticore.MCTask>("grammar").apply {
        grammar.set(file(project(":languages").projectDir.toString() + "/grammars/" +  grammarName))
        outputs.upToDateWhen { incCheck(grammarName) }
      }
    }
  }
}