/* (c) https://github.com/MontiCore/monticore */
import montiarc.gradle.cd2pojo.CD2PojoCompile
import montiarc.gradle.ma2jsim.MontiArcCompile
import montiarc.gradle.ma2jsim.compileMontiArcTaskName
import montiarc.gradle.ma2jsim.SourceSetSupport.Companion.linkSourceSets

plugins {
  id("cd2pojo")
  id("montiarc-jsim")
  id("montiarc.build.integration-test")
  id("montiarc.build.java-test-fixtures")
}

var variabilitySources: SourceSet? = null

sourceSets {
  main {
    cd2pojo {
      setSrcDirs(setOf("$projectDir/main/cd2pojo"))
    }
    montiarc {
      setSrcDirs(setOf("$projectDir/main/montiarc"))
    }
  }
  variabilitySources = create("variability") {
    cd2pojo {
      setSrcDirs(setOf("$projectDir/variability/cd2pojo"))
    }
    montiarc {
      setSrcDirs(setOf("$projectDir/variability/montiarc"))
    }
  }
  test {
    cd2pojo {
      setSrcDirs(setOf("$projectDir/test/cd2pojo"))
    }
    montiarc {
      setSrcDirs(setOf("$projectDir/test/montiarc"))
    }
  }

  linkSourceSets(main.get(), variabilitySources!!)
  linkSourceSets(variabilitySources!!, test.get())
}

dependencies {
  implementation(libs.guava)
  implementation(libs.janino)
  testImplementation(testFixtures(project(":libraries:simulator-rte")))
}

tasks.withType(CD2PojoCompile::class).configureEach {
  useClass2Mc.set(true)
}

tasks.withType(MontiArcCompile::class).configureEach  {
  useClass2Mc.set(true)
}

tasks.named(variabilitySources!!.compileMontiArcTaskName, MontiArcCompile::class.java) {
  checkVariability.set(true)
}
