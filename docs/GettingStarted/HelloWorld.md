<!-- (c) https://github.com/MontiCore/monticore -->
# Hello, World!
You're now ready to start with your first system. 

## Writing and Running a Component Model
Go to a directory of your choice and create a text file named `HelloWorld.arc`.

Now open the file `HelloWorld.arc` you just created and insert the following:

```montiarc
--8<-- "applications/tutorial/main/montiarc/HelloWorld.arc:2"
```

Save the file and open a terminal window inside your folder and enter the following commands (one by one) to compile and run the model:

```bash
montiarc --input HelloWorld.arc --output . # (1)!
montiarc run DeployHelloWorld.java
```

1.  If you've downloaded the jar directly, run `java -jar MontiArc-7.8.0.jar --input HelloWorld.arc --output .`

A `Hello, World!` should print to the terminal. If it did, congrats! You're now a MontiArc modeler.

## Anatomy of a Component Model
Let's review the model you've just run.


#### Import
```montiarc
import montiarc.lang.*;
```
The first part is an import statement. It allows us to include additional functionality inside our model.
Every element is located in some package. Everything defined in the same package is visible to one another and can be used without importing.
Elements located in another package must be imported explicitly, like the MontiArc standard library that is imported here.

#### Comment

[Comments](../Reference/Concepts/Comments.md) will be ignored by the parser and are just like in Java and many other languages.

```montiarc
// My HelloWorld component
```

#### Component

```montiarc
component HelloWorld {

}
```
The main part is a [component](../Reference/Component/index.md) called `HelloWorld`. Components are the core building blocks of any MontiArc model and describe a unit of common characteristics that interact with the environment through well-defined interfaces.
In our example, the component has no interface and thus is deployable, i.e. executable.

Components can consist of other components, which we refer to as [decomposed](../Reference/Component/Decomposition.md) components, or contain a behavior description, so-called [atomic](../Reference/Behavior/index.md) components. In our example, the component is atomic, and the behavior is described by an [automaton](../Reference/Behavior/Automata.md).

#### Automaton

```montiarc
automaton {
  initial state S;
  S -> S / {

  }
}
```
Automata have [states](../Reference/Behavior/Automata.md#states) and [transitions](../Reference/Behavior/Automata.md#transitions). States can be identified by the `state` keyword and their name. One state has to be marked as the `initial` state; this is the state we start at.

Transitions describe state changes and allow execution of code during that. Every transition has a trigger; more on that later, but if none is specified (as in our case), the transition is triggered every time step.
In our example, we stay at state `S` and execute the transition body.

#### Statements
A transition body consists of multiple [statements](../Reference/Concepts/Statements.md). Where each ends with a semicolon `;`, which means the statement is over, and we can continue with the next one.
Our body consists of two statements:

```montiarc
Console.printLn("Hello World!");
```
This line does most of the work in our model; it prints the string `"Hello World!"` to the console. It is a function call similar to other programming languages.
What is printed out can be provided as an argument to the method.

```montiarc
Simulation.stop();
```
This statement interacts with the simulation we are executing and stops it after the current time step. By default, models are run indefinitely, and without this statement, we would have repeatedly printed `Hello World!` to the console forever.

You most likely won't need to interact with the simulation, yet stop it, in your projects since modeling infinite systems is a feature, not a bug.

## Compiling and Running are Separate Steps
Now that you know how the program was modeled, let's again look at the steps you took to execute the model. 

Although we refer to it as execution, this is more of a simulation. In real applications, these models are cyberphysical systems where the components are distributed and possibly realized through mechanical, electrical, and software components. 

```bash
montiarc --input HelloWorld.arc --output .
```
Here, you used the MontiArc command-line tooling to compile MontiArc models into Java code. This creates the following files:

```bash
DeployHelloWorld.java
DeployMqttHelloWorld.java
DeployRestHelloWorld.java
HelloWorldComponent.java
HelloWorldContext.java
HelloWorldBehavior.java
HelloWorldAutomaton.java
HelloWorldStates.java
HelloWorldEvents.java
HelloWorldSyncMsg.java
```

Most of which are needed to simulate the model using Java.

```bash
montiarc run DeployHelloWorld.java
```
This executes the Java main method of the `DeployHelloWorld` class, which in turn sets up the [simulation](../Usage/Simulation/index.md) and starts it.
The run command is shorthand for calling Java with the correct class path. It requires Java to be installed on your system.

By default the simulation is non-interactive and input streams have to be provided for it to be useful (See [testing](../Reference/Testing/index.md)).
We provide two interactive simulation deployments where the simulation reads and writes to [MQTT](../Usage/Simulation/index.md#mqtt) or has a [Rest API](../Usage/Simulation/index.md#rest).


---

Now that you have a basic overview, let's look at how to make the project easier.