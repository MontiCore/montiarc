/* (c) https://github.com/MontiCore/monticore */
package montiarc.tooling.plugin

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

abstract class MontiarcCompile : JavaExec() {
  @get:InputFiles
  @get:SkipWhenEmpty
  @get:IgnoreEmptyDirectories
  abstract val modelPath : ConfigurableFileCollection

  @get:InputFiles
  @get:IgnoreEmptyDirectories
  abstract val symbolImportDir : ConfigurableFileCollection

  @get:Input
  abstract val useClass2Mc : Property<Boolean>

  @get:InputFiles
  @get:IgnoreEmptyDirectories
  abstract val hwcPath : ConfigurableFileCollection

  @get:OutputDirectory
  abstract val outputDir : DirectoryProperty

  @get:Input
  abstract val sourceSetName: Property<String>

  init {
    description = "Generates .java code from MontiArc models."

    classpath(project.configurations.getByName(MontiarcPlugin.configName))
    mainClass.convention(MontiarcPlugin.maToolClass)

    useClass2Mc.convention(true)
    outputDir.convention(project.layout.buildDirectory.dir("montiarc"))
    sourceSetName.convention(SourceSet.MAIN_SOURCE_SET_NAME)

    // In the following we will set default values that are based on the configured value of sourceSetName. As
    // we want to use the name that the user specifies, only using the *default* value that we just set, we use
    // a closure (curly braces around the value {}). The effect is, that the path expression only gets evaluated *after*
    // the user has applied his configuration. Therefore, our default value depends on the *actual* value of
    // sourceSetName. This mechanism is similar to gradle's lazy configuration strategy.
    modelPath.from({"${project.projectDir}/src/${sourceSetName.get()}/montiarc"})
    symbolImportDir.from({"${project.projectDir}/src/${sourceSetName.get()}/symbols"})
    if (project.pluginManager.hasPlugin("java")) {
      hwcPath.from({
        project.extensions
          .getByType(JavaPluginExtension::class.java)
          .sourceSets.getByName(sourceSetName.get())
          .allJava.sourceDirectories
          .filter { dir -> !dir.absoluteFile.equals(outputDir.get().asFile.absoluteFile) }
          // TODO: try using an exclude pattern for outputdir/**
      })
    } else {
      hwcPath.from({"${project.projectDir}/src/${sourceSetName.get()}/java"})
    }
  }

  @TaskAction
  override fun exec() {

    //printInfo()

    args("-mp", this.modelPath.asPath)
    args("-path", this.symbolImportDir.asPath)
    args("-o", this.outputDir.asFile.get().path)
    args("-hwc", this.hwcPath.asPath)
    if(useClass2Mc.get()) { args("-c2mc"); }

    super.exec()
  }

  private fun printInfo() {
    println("Trying generation")
    println("Modelpath:")
    modelPath.forEach { println("  $it") }
    println("HWCpath:")
    hwcPath.forEach { println("  $it") }
    println("class2mc: " + useClass2Mc.get())
    println("Symbol import dir:")
    symbolImportDir.forEach { println("  $it") }
    println("OutDir: " + outputDir.get())

    println("MainClass:" + mainClass.get())
    println("ClassPath:")
    classpath.asPath.split(":").forEach { println("  $it") }
  }
}