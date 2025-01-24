/* (c) https://github.com/MontiCore/monticore */
import montiarc.gradle.cd2pojo.Cd2PojoCompile
import montiarc.gradle.ma2jsim.MontiArcCompile
import montiarc.gradle.ma2jsim.compileMontiarcTaskName
import montiarc.gradle.ma2jsim.SourceSetSupport.Companion.linkSourceSets

plugins {
  id("cd2pojo")
  id("montiarc-jsim")
  id("montiarc.build.integration-test")
  id("java-test-fixtures")
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
  testFixtures {
    java {
      setSrcDirs(setOf("$projectDir/testFixtures/java"))
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
}

cd2pojo {
  internalMontiArcTesting.set(true)
}

montiarc {
  internalMontiArcTesting.set(true)
}

val enableAttachDebugger = false

tasks.withType(Cd2PojoCompile::class.java) {
  useClass2Mc.set(true)

  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }
}

tasks.withType(MontiArcCompile::class.java) {
  useClass2Mc.set(true)

  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }
}

tasks.named(variabilitySources!!.compileMontiarcTaskName, MontiArcCompile::class.java) {
  checkVariability.set(true)
}

