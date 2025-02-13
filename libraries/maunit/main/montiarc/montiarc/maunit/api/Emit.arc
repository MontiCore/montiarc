/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

component Emit<T>(T output) {
  port sync out T out;

  automaton {
    initial state S;
    S -> S / {
      out = output;
    };
  }
}
