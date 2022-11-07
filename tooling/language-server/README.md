<!-- (c) https://github.com/MontiCore/monticore -->
# Language Server for MontiArc
To start IntelliJ with the generated plugin, you can execute the gradle task 
`runMontiArcIntellijPluginAttached`. This task opens the `./example` project. 
The language server can be debugged by running 
`runMontiArcIntellijPluginAttached` with a debugger attached.

## Installing the packaged plugin
1. [Download](https://git.rwth-aachen.de/monticore/montiarc/core/-/jobs/artifacts/develop/raw/tooling/language-server/build/generated-sources/MontiArc/plugins/montiarc-intellij-plugin/build/distributions/MontiArc-intellij-plugin-1.0-SNAPSHOT.zip?job=Build%20jdk17) the latest build from the `develop` branch pipeline artifacts
2. Set the environment variable `LSP_JAVA_HOME` to the base of a Java 17 installation
3. Install the plugin: https://www.jetbrains.com/help/idea/managing-plugins.html#install_plugin_from_disk
