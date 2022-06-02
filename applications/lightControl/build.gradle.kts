/* (c) https://github.com/MontiCore/monticore */

plugins {
    id("montiarc.build.kotlin-library")
}

val hwcDir = "$projectDir/src/main/java"
val genDir = "$buildDir/generated-sources"
val impDir = "$buildDir/models"

val generatorLogbackConfig = "$projectDir/logback.xml"
val generatorLogbackOutDir = "$buildDir/logs"

// Configuration(s)
val generateKT = configurations.create("generateKT")
val models = configurations.create("models")

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

    models(project(":libraries:dynsim-reaction-rte")) {
        capabilities { requireCapability(libs.dynsimRteModel) }
    }
}

val generateKotlin = tasks.register<JavaExec>("generateKotlin") {
    classpath(generateKT)
    mainClass.set("montiarc.generator.ma2kotlin.ModeArcTool")

    args("-mp", "$projectDir/main/resources")
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

val unpackLibModelsTask = tasks.register<Sync>("unpackmodels") {
    dependsOn(models)

    from( models.map { zipTree(it) } )
    into(genDir)
    exclude("META-INF/")
}

// Setting up task dependencies
tasks.compileJava { dependsOn(generateKotlin) }
tasks.compileKotlin { dependsOn(generateKotlin) }

generateKotlin {dependsOn(unpackLibModelsTask)}
generateKotlin { mustRunAfter(project(":generators:ma2ktln").tasks.withType(Test::class)) }

kotlin.sourceSets["main"].kotlin.srcDir(genDir)
