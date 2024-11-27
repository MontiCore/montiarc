/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

component Emit<T>(T output) {
  port out T out;

  <<sync>> automaton {
    initial state S;
    S -> S / {
      out = output;
    };
  }
}
