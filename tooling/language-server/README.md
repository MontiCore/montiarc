<!-- (c) https://github.com/MontiCore/monticore -->
# Language Server for MontiArc + CD4A
To start IntelliJ with the generated plugin, you can execute the gradle task 
`runMontiArcWithCD4AIntellijPluginAttached`. This task opens the `./example` project. 
The language server can be debugged by running 
`runMontiArcWithCD4AIntellijPluginAttached` with a debugger attached.

## Installing the packaged plugin
1. [Download](https://git.rwth-aachen.de/monticore/montiarc/core/-/jobs/artifacts/develop/raw/tooling/language-server/build/generated-sources/MontiArcWithCD4A/plugins/montiarcwithcd4a-intellij-plugin/build/distributions/MontiArcWithCD4A-intellij-plugin-1.0-SNAPSHOT.zip?job=Build%20jdk17) the latest build from the `develop` branch pipeline artifacts
2. Set the environment variable `LSP_JAVA_HOME` to the base of a Java 17 installation
3. Install the plugin: https://www.jetbrains.com/help/idea/managing-plugins.html#install_plugin_from_disk
