<!-- (c) https://github.com/MontiCore/monticore -->
# Simulating The System

To simulate the system, we can use the Gradle run task like before.
But since we use the command line to interact with the system, we need to edit the Gradle build file.
While we are at it, we can also set the main class to the deployment elevator system class. 

=== "build.gradle.kts"
    ```kotlin
    application {
      mainClass.set("elevator.DeployElevatorSystem")
    }

    val run by tasks.getting(JavaExec::class) {
      standardInput = System.`in`
    }
    ```

We can then run the simulation by simply executing:
=== "Windows"
    ```powershell
    .\gradlew.bat -q --console plain run
    ```
=== "macOS/Linux"
    ```bash
    ./gradlew -q --console plain run
    ```