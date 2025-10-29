/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

component Number2Int<T extends Number> {
  port
   sync in T i,
   sync out int o;

  automaton {
    initial state S;

    S -> S / { o = i.intValue(); }
  }
}
