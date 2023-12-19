/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

component GenericForwarder<T> {
  port
   in T i,
   out T o;

  <<sync>> automaton {
    initial state S;

    S -> S / { o = i; };
  }
}
