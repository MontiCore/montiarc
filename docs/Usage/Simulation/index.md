---
icon: material/animation-play
---
<!-- (c) https://github.com/MontiCore/monticore -->

The included simulator allows running MontiArc models. This is useful for testing.
Multiple options are available for running the simulator, including interactive.
A simulator has the benefit of controlling the simulation speed.

For more information about the simulator read the [reference chapter](../../Reference/Simulation/index.md)

## Non-Interactive
The default simulator is not interactive and has to be filled with inputs programmatically.
See the [testing](../../Reference/Testing/index.md) chapter for more information on it.

## Interactive
On the other hand, the simulator can also be run interactively.
This might be useful for prototyping your models. You can control how fast the simulation is running by
setting the tick length. 

Two different backends are available to interact with the simulation.
For every backend and component a java class with a main method is generated.
These can be found in your build folder. 
To run the simulation simply execute the main method using your IDE. 

#### REST

Due to the nature of REST, this backend can only be used to write to ports,
but not read from them. To see how the systems behaves, you will have to look at the log.

#### MQTT

The [MQTT](https://mqtt.org/) backend requires a running MQTT broker. The simulator connects to it and reads from and writes to topics.
