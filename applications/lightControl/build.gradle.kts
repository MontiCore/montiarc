/* (c) https://github.com/MontiCore/monticore */

plugins {
    id("org.jetbrains.kotlin.jvm").version("1.6.0")
    id("montiarc.build.modules")
    id("montiarc.build.publishing")
    id("montiarc.build.repositories")
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
    generateKT(project(":generators:ma2ktln"))
    generateKT("${libs.logbackCore}:${libs.logbackVersion}")
    generateKT("${libs.logbackClassic}:${libs.logbackVersion}")

    api(project(":libraries:dynsim-rte"))
    implementation("${libs.kotlinCoroutines}:${libs.kotlinxVersion}")
    implementation("${libs.kotlinStdlib}:${libs.kotlinVersion}")
    implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")
    implementation("${libs.seCommonsUtils}:${libs.monticoreVersion}")

    testImplementation("${libs.kotlinJunit}:${libs.kotlinVersion}")
}

val generateKotlin = tasks.register<JavaExec>("generateKotlin") {
    classpath(generateKT)
    mainClass.set("montiarc.generator.ma2kotlin.ModeArcTool")

    args("-mp", "$projectDir/src/main/resources", /*"$buildDir/$models_classifier",*/)
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
