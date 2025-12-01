/* (c) https://github.com/MontiCore/monticore */
import montiarc.lang.*;

component HelloWorld {
  automaton {
    initial state S;
    S -> S / {
      Console.printLn("Hello World!");
      Simulation.stop();
    }
  }
}
