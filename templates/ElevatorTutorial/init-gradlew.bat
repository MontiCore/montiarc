@rem
@rem (c) https://github.com/MontiCore/monticore
@rem
@rem This tool downloads the Gradle 8.14.4 wrapper

@echo off

REM Get the directory of the current script
set BASEDIR=%~dp0
mkdir "%BASEDIR%gradle\wrapper\"

REM Download files using curl
curl -o "%BASEDIR%gradle\wrapper\gradle-wrapper.jar" https://raw.githubusercontent.com/gradle/gradle/refs/tags/v8.14.4/gradle/wrapper/gradle-wrapper.jar
curl -o "%BASEDIR%gradlew" https://raw.githubusercontent.com/gradle/gradle/refs/tags/v8.14.4/gradlew
curl -o "%BASEDIR%gradlew.bat" https://raw.githubusercontent.com/gradle/gradle/refs/tags/v8.14.4/gradlew.bat

(
  echo distributionBase=GRADLE_USER_HOME
  echo distributionPath=wrapper/dists
  echo distributionUrl=https\://services.gradle.org/distributions/gradle-8.14.4-bin.zip
  echo networkTimeout=10000
  echo zipStoreBase=GRADLE_USER_HOME
  echo zipStorePath=wrapper/dists
) > "%BASEDIR%gradle\wrapper\gradle-wrapper.properties"
