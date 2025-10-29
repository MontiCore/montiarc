/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.actions;

component InitialAndEntryAction {
  port sync out String o;

  automaton {

    initial { o = "INIT"; } state A {
      entry / o = "Enter A";
    }

    state B { entry / o = "Enter B"; }

    A -> B / o = "A -> B";
    B -> A / o = "B -> A";
  }
}
