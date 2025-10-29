/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.actions;

component InitialAndEntryActionWithDelay {
  port in String i;
  port out String o;

  <<delayed>> automaton {

    initial { o = "INIT"; } state A {
      entry / o = "Enter A";
      do / o = "Do A";
    }

    state B {
      entry / o = "Enter B";
      do / o = "Do B";
    }

    A -> B [i.equals("trigger")] i / o = "A -> B";
    B -> A [i.equals("trigger")] i / o = "B -> A";
  }
}
