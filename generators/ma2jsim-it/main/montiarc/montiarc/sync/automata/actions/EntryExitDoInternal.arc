/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.actions;

component EntryExitDoInternal {
  port in String i;
  port out String o;

  <<sync>> automaton {

    initial state A {
      entry / o = "Enter A";
      do / o = "Do A";
      exit / o = "Exit A";

      -> [i.equals("internal")] / o = "internal A";;
    };

    state B {
      entry / o = "Enter B";
      do / o = "Do B";
      exit / o = "Exit B";

      -> [i.equals("internal")] / o = "internal B";;
    };

    A -> B [i.equals("switch")] / o = "A -> B";;
    B -> A [i.equals("switch")] / o = "B -> A";;

    A -> A [i.equals("loop")] / o = "A -> A";;
    B -> B [i.equals("loop")] / o = "B -> B";;
  }
}
