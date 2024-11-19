/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.ma2jsim

import montiarc.gradle.cd2pojo.Cd2PojoCompile
import montiarc.gradle.cd2pojo.cd2PojoDependencyDeclarationConfigName
import montiarc.gradle.cd2pojo.compileCd2PojoTaskName
import montiarc.gradle.montiarc.cd2pojo4MaDeclarationConfigName
import montiarc.gradle.montiarc.montiarcDependencyDeclarationConfigName
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet

/**
 * Contains support methods that plugin users can use to handle source sets.
 * They can, for example, link different source sets and corresponding tasks together.
 */
class SourceSetSupport {
  companion object {

    /**
     * Makes all produced elements of [provider] available to the respective [consumer] tasks.
     *
     * This includes class diagrams, MontiArc models, and Java code.
     */
    fun Project.linkSourceSets(provider: SourceSet, consumer: SourceSet) {
      assertPluginIsApplied("montiarc-jsim")
      assertPluginIsApplied("cd2pojo")
      assertPluginIsApplied("java")

      putProducedJavaIntoConsumerInput(provider, consumer)
      putProducedModelsIntoConsumerInput(provider, consumer)
      transferProducerDependenciesToConsumer(provider, consumer)
    }

    private fun Project.assertPluginIsApplied(pluginId: String) {
      if (!pluginManager.hasPlugin(pluginId)) {
        throw GradleException("Plugin '$pluginId' must be applied to link source sets by MontiArc.")
      }
    }

    private fun putProducedJavaIntoConsumerInput(provider: SourceSet, consumer: SourceSet) {
      consumer.compileClasspath += provider.output
      consumer.runtimeClasspath += provider.output
    }

    private fun Project.putProducedModelsIntoConsumerInput(provider: SourceSet, consumer: SourceSet) {
      val providerCdSymbols = provider {
        tasks.named(provider.compileCd2PojoTaskName, Cd2PojoCompile::class.java).get()
          .symbolOutputDir()
      }
      val providerMaSymbols = provider {
        tasks.named(provider.compileMontiarcTaskName, MontiArcCompile::class.java).get()
          .symbolOutputDir()
      }

      tasks.named(consumer.compileCd2PojoTaskName, Cd2PojoCompile::class.java).configure {
        it.symbolImportDir.from(providerCdSymbols)
      }
      tasks.named(consumer.compileMontiarcTaskName, MontiArcCompile::class.java).configure {
        it.symbolImportDir.from(providerCdSymbols, providerMaSymbols)
      }
    }

    private fun Project.transferProducerDependenciesToConsumer(provider: SourceSet, consumer: SourceSet) {
      configurations.named(consumer.implementationConfigurationName).configure {
        it.extendsFrom(configurations.getByName(provider.implementationConfigurationName))
      }
      configurations.named(consumer.cd2PojoDependencyDeclarationConfigName).configure {
        it.extendsFrom(configurations.getByName(provider.cd2PojoDependencyDeclarationConfigName))
      }
      configurations.named(consumer.cd2pojo4MaDeclarationConfigName).configure {
        it.extendsFrom(configurations.getByName(provider.cd2pojo4MaDeclarationConfigName))
      }
      configurations.named(consumer.montiarcDependencyDeclarationConfigName).configure {
        it.extendsFrom(configurations.getByName(provider.montiarcDependencyDeclarationConfigName))
      }
    }
  }
}