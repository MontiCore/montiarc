/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.transition;

component InnerTransitionActions {
  port sync in int i;
  port sync out String o;

  automaton {
    initial state S {
      entry / o = "Entry_S";
      exit / o = "Exit_S";
      -> / { o = "Inner Trans"; }
    }
  }
}
