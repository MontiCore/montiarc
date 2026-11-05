/* (c) https://github.com/MontiCore/monticore */
import montiarc.lang.*;

// My HelloWorld component
<<deploy>> component HelloWorld {
  automaton {
    initial state S;
    S -> S / {
      Console.printLn("Hello World!");
      Simulation.stop();
    }
  }
}
