/* (c) https://github.com/MontiCore/monticore */

val se_commons_version: String by project
val junit_jupiter_version: String by project
val logback_version: String by project
val kotlin_version: String by project
val kotlinx_version: String by project

plugins {
    id("org.jetbrains.kotlin.jvm").version("1.6.0")
}

group = "montiarc.applications"

val hwcDir = "$projectDir/src/main/java"
val genDir = "$buildDir/generated-sources"

val generatorLogbackConfig = "$projectDir/logback.xml"
val generatorLogbackOutDir = "$buildDir/logs"

sourceSets["main"].java {
    srcDir("$genDir")
}

// Configuration(s)
val generateKT = configurations.create("generateKT")

dependencies {
    // MontiCore dependencies
    implementation("de.se_rwth.commons:se-commons-logging:$se_commons_version")
    implementation("de.se_rwth.commons:se-commons-utilities:$se_commons_version")

    // Internal dependencies
    generateKT(project(":generators:ma2ktln"))
    implementation(project(":language:montiarc"))
    implementation(project(":libraries:majava-rte"))
    implementation(project(":libraries:dynsim-rte"))

    // Other dependencies
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinx_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    // Loggers for generators
    generateKT("ch.qos.logback:logback-core:$logback_version")
    generateKT("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

val generateKotlin = tasks.register<JavaExec>("generateKotlin") {
    classpath(generateKT)
    mainClass.set("montiarc.generator.ma2kotlin.ModeArcTool")

    args("-mp", "$projectDir/src/main/resources", /*"$buildDir/$librarymodels_classifier",*/)
    args("-path", genDir)
    // args("-o", genDir)
    // args("-hwc", hwcDir)
    args("-c2mc")
    outputs.dir(genDir)

    // Configuring logging during generation
    systemProperties["logback.configurationFile"] = generatorLogbackConfig
    systemProperties["LOGBACK_TARGET_DIR"] = generatorLogbackOutDir
    systemProperties["LOGBACK_TARGET_FILE_NAME"] = "logback-ma2ktln"
}

// Setting up task dependencies
tasks.compileJava { dependsOn(generateKotlin) }
tasks.compileKotlin { dependsOn(generateKotlin) }

generateKotlin { mustRunAfter(project(":generators:ma2ktln").tasks.withType(Test::class)) }
