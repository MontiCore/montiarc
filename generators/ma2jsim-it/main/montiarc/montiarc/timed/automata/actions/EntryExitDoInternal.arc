/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.actions;

component EntryExitDoInternal {
  port in String i;
  port out String o;

  <<timed>> automaton {

    initial state A {
      entry / o = "Enter A";
      do / o = "Do A";
      exit / o = "Exit A";

      -> [i.equals("internal")] i / o = "internal A";;
    };

    state B {
      entry / o = "Enter B";
      do / o = "Do B";
      exit / o = "Exit B";

      -> [i.equals("internal")] i / o = "internal B";;
    };

    A -> B [i.equals("switch")] i / o = "A -> B";;
    B -> A [i.equals("switch")] i / o = "B -> A";;

    A -> A [i.equals("loop")] i / o = "A -> A";;
    B -> B [i.equals("loop")] i / o = "B -> B";;
  }
}
