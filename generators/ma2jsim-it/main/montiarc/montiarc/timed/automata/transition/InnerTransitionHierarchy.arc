/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

component InnerTransitionHierarchy {
  port in int i;
  port out String o;

  automaton {
    initial state S {
      entry / o = "Entry_S";
      exit / o = "Exit_S";
      -> [i == 1] i / { o = "Inner Trans 1"; }
      -> i / { o = "Inner Trans 2"; }

      initial state A {
        entry / o = "Entry_A";
        exit / o = "Exit_A";
      }

      state B {
        entry / o = "Entry_B";
        exit / o = "Exit_B";
      }

      A -> B [i > 1] i / { o = "Trans_A_B"; }
    }
  }
}
