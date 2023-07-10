/* (c) https://github.com/MontiCore/monticore */
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

val TARGET_JVM_VERSION: Int = 11

pluginManager.withPlugin("java") {
  with (extensions.getByType(JavaPluginExtension::class.java)) {
    toolchain.languageVersion.set(JavaLanguageVersion.of(TARGET_JVM_VERSION))
  }
}

pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
  with (extensions.getByType(KotlinJvmProjectExtension::class.java)) {
    jvmToolchain(TARGET_JVM_VERSION)
  }
}