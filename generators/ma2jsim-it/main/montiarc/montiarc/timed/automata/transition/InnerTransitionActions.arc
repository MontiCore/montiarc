/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

component InnerTransitionActions {
  port in int i;
  port out String o;

  automaton {
    initial state S {
      entry / o = "Entry_S";
      exit / o = "Exit_S";
      -> i / { o = "Inner Trans"; }
    }
  }
}
