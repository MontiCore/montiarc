/* (c) https://github.com/MontiCore/monticore */
import montiarc.build.Language.Companion.configureMCTask



plugins {
    id("montiarc.build.language")
    id("montiarc.build.java-library")
    id("montiarc.build.shadow")
}

buildDir = file(project(":languages").buildDir.toString() + "/${project.name}")

dependencies {
    grammar(libs.mc.grammar) {
        capabilities { requireCapability("de.monticore:monticore-grammar-grammars") }
    }
    grammar(libs.mc.sc) {
        capabilities { requireCapability("de.monticore.lang:statecharts-grammars") }
    }

    api(project(":languages:montiarc"))
    api(project(":languages:basis"))
    api(project(":languages:conformance"))
    api(libs.mc.sc) {
        exclude("org.apache.groovy", "groovy")
    }

    implementation(libs.apache)
    implementation(libs.jgrapht.core)
    implementation(libs.jgrapht.jgraphx)
    implementation(libs.jgrapht.ext)
    implementation(libs.guava)
    implementation(libs.z3)
    implementation(libs.mc.cd4a)
    implementation(libs.mc.ocl.ocl2smt)
    implementation(variantOf(libs.mc.cd4a) {classifier("cd2smt")})

    configureMCTask("../effect/grammars/MCEffect.mc4")
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "mceffect.MCEffectTool"
    }
    isZip64 = true
    archiveClassifier.set("mc-tool")
    archiveBaseName.set("MCEffect")
    archiveFileName.set( "${archiveBaseName.get()}.${archiveExtension.get()}" )
}

tasks.register<JavaExec>("runSteamboilderDemo") {
    classpath = files(tasks.shadowJar.get().archiveFile)
    val modelDir = "test/resources/mceffect/demo/"

    args = listOf(
        "-mp", modelDir+"steamboiler/",
        "-mc", "demo.steamboiler.SteamBoiler",
        "-e",  "SteamBoiler.eff"
        // ,"-g"   // comment in to show graph
    )
}

tasks.register<JavaExec>("runDemo") {
    classpath = files(tasks.shadowJar.get().archiveFile)
    val modelDir = "test/mceffect/demo/"

    args = listOf(
        "-mp", modelDir+"runningexample/",
        "-mc", "demo.runningexample.Comp0",
        "-e", "Comp0.eff"
        // ,"-g"   // comment in to show graph
    )
}
