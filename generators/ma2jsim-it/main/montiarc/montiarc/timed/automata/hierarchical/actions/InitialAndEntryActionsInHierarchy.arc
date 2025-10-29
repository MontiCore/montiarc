/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical.actions;

component InitialAndEntryActionsInHierarchy {
  port in String i;
  port out String o;

  automaton {

    initial { o = "INIT A"; } state A {
      entry / o = "-> A";
      initial { o = "INIT AA"; } state AA { entry / o = "-> AA"; }
    }

    state B;

    A -> B / o = "A -> B";
    B -> A / o = "B -> A";

    A -> B i / o = "A -> B";
    B -> A i / o = "B -> A";

  }
}
