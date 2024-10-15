/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical;

component NoEnabledTransition {

  port in String i;
  port out String o;

  <<timed>> automaton {

    initial state INIT;
    state Target;

    state a { initial state aa; };
    state b { initial state bb { initial state bbb; }; };
    state c { initial state cc { initial state ccc { initial state ccc_c; }; }; };
    state d { initial state dd { initial state ddd { initial state ddd_d { initial state ddd_dd; }; }; }; };

    INIT -> aa     [i.equals("aa")]     i / o = "-> aa";;
    INIT -> bbb    [i.equals("bbb")]    i / o = "-> bbb";;
    INIT -> ccc_c  [i.equals("ccc_c")]  i / o = "-> ccc_c";;
    INIT -> ddd_dd [i.equals("ddd_dd")] i / o = "-> ddd_dd";;

    // Transitions to check in which state one is
    aa -> aa         [i.equals("check")] i / o = "aa";;
    bbb -> bbb       [i.equals("check")] i / o = "bbb";;
    ccc_c -> ccc_c   [i.equals("check")] i / o = "ccc_c";;
    ddd_dd -> ddd_dd [i.equals("check")] i / o = "ddd_dd";;

    // Transitions that should not be enabled in our test cases:
    a -> Target [i.equals("T")] i / o = "a -> Target";;
    b -> Target [i.equals("T")] i / o = "b -> Target";;
    c -> Target [i.equals("T")] i / o = "c -> Target";;
    d -> Target [i.equals("T")] i / o = "d -> Target";;

    aa -> Target [i.equals("T")] i / o = "aa -> Target";;
    bb -> Target [i.equals("T")] i / o = "bb -> Target";;
    cc -> Target [i.equals("T")] i / o = "cc -> Target";;
    dd -> Target [i.equals("T")] i / o = "dd -> Target";;

    bbb -> Target [i.equals("T")] i / o = "bbb -> Target";;
    ccc -> Target [i.equals("T")] i / o = "ccc -> Target";;
    ddd -> Target [i.equals("T")] i / o = "ddd -> Target";;

    ccc_c -> Target [i.equals("T")] i / o = "ccc_c -> Target";;
    ddd_d -> Target [i.equals("T")] i / o = "ddd_d -> Target";;
    ddd_dd -> Target [i.equals("T")] i / o = "ddd_dd -> Target";;
  }
}
