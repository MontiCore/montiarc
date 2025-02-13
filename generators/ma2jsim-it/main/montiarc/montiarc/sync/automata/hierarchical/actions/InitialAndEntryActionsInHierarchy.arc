/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical.actions;

component InitialAndEntryActionsInHierarchy {

  port sync out String o;

  automaton {

    initial { o = "INIT A"; } state A {
      entry / o = "-> A";
      initial { o = "INIT AA"; } state AA { entry / o = "-> AA"; } ;
    };

    state B;

    A -> B / o = "A -> B";;
    B -> A / o = "B -> A";;

  }
}
