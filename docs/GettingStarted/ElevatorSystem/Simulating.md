<!-- (c) https://github.com/MontiCore/monticore -->
# Simulating the System

To [simulate the system](../../Reference/Simulation/index.md), we can use the Gradle run task like before.
To make life easier, we can also set the main class to the deployment elevator system class. 

=== "build.gradle.kts"
    ```kotlin
    application {
      mainClass.set("elevator.DeployRestElevatorSystem")
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

And send messages to the system, i.e., request the elevator to the first floor, with:
=== "Windows"
    ```powershell
    curl http://localhost:8020/ElevatorSystem/pressedOnFloor -Body '1' -Method Post
    ```
=== "macOS/Linux"
    ```bash
    curl -X POST http://localhost:8020/ElevatorSystem/pressedOnFloor -d '1'
    ```
