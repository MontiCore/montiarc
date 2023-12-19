/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

component Number2Int<T extends Number> {
  port
   in T i,
   out int o;

  <<sync>> automaton {
    initial state S;

    S -> S / { o = i.intValue(); };
  }
}
