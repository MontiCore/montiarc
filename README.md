# MontiArc Core Project

The MontiArc Core repository contains everything related to the common basis of the MontiArc architecture description language. This project is maintained by the [Working Group for Model-Driven Systems Engineering (MDSE)][mdse].

Contact: @puetzer, @david.schmalzing, @wortmann

[se-rwth]: http://www.se-rwth.de
[mdse]:http://www.se-rwth.de/teams/mdse/

## The MontiArc Architecture Description Language

<img src="pics/elevatorExample.PNG" alt="drawing" height="400px"/>

In MontiArc, architectures are described as component and connector systems in which autonomously acting components perform computations. Communication between components is regulated by connectors between the components’ interfaces, which are stable and build up by typed, directed ports. Components are either atomic or composed of connected subcomponents. Atomic components yield behavior descriptions in the form of embedded time-synchronous port automata, embedded JavaDSL models, or via integration of handcrafted code. For composed components the behavior emerges from the behavior of their subcomponents. 

## Project Structure

* languages/
  * montiarc-fe
* generators/
    * cd2pojo
    * maa2java
* applications/
    * bumperbot  
* libraries/
    * maa-rte
    * lejos-rte
    * simulator-rte
    * ...

# copyright

© Copyright 2019 by [Chair of Software Engineering at RWTH Aachen University][se-rwth].