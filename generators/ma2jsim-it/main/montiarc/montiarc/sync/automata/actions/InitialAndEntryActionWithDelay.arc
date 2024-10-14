/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.actions;

component InitialAndEntryActionWithDelay {
  port <<delayed>> out String o;

  <<sync>> automaton {

    initial { o = "INIT"; } state A {
      entry / o = "Enter A";
      do / o = "Do A";
    };

    state B {
      entry / o = "Enter B";
      do / o = "Do B";
    };

    A -> B / o = "A -> B";;
    B -> A / o = "B -> A";;
  }
}
