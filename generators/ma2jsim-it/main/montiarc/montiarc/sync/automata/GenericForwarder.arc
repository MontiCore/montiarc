/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

component GenericForwarder<T> {
  port
   sync in T i,
   sync out T o;

  automaton {
    initial state S;

    S -> S / { o = i; };
  }
}
