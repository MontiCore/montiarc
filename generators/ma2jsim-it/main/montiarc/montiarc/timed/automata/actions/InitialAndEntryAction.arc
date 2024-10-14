/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.actions;

component InitialAndEntryAction {
  port in String i;
  port out String o;

  <<timed>> automaton {

    initial { o = "INIT"; } state A {
      entry / o = "Enter A";
    };

    state B { entry / o = "Enter B"; };

    A -> B i / o = "A -> B";;
    B -> A i / o = "B -> A";;
  }
}
