/* (c) https://github.com/MontiCore/monticore */
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

val jvmversion: Int = 17

pluginManager.withPlugin("java") {
  with (extensions.getByType(JavaPluginExtension::class.java)) {
    toolchain.languageVersion.set(JavaLanguageVersion.of(jvmversion))
  }
}

pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
  with (extensions.getByType(KotlinJvmProjectExtension::class.java)) {
    jvmToolchain(jvmversion)
  }
}
