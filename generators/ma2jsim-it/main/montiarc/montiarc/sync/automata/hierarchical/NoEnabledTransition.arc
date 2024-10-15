/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical;

component NoEnabledTransition {

  port in String i;
  port out String o;

  <<sync>> automaton {

    initial state INIT;
    state Target;

    state a { initial state aa; };
    state b { initial state bb { initial state bbb; }; };
    state c { initial state cc { initial state ccc { initial state ccc_c; }; }; };
    state d { initial state dd { initial state ddd { initial state ddd_d { initial state ddd_dd; }; }; }; };

    INIT -> aa     [i.equals("aa")]     / o = "-> aa";;
    INIT -> bbb    [i.equals("bbb")]    / o = "-> bbb";;
    INIT -> ccc_c  [i.equals("ccc_c")]  / o = "-> ccc_c";;
    INIT -> ddd_dd [i.equals("ddd_dd")] / o = "-> ddd_dd";;

    // Transitions to check in which state one is
    aa -> aa         [i.equals("check")] / o = "aa";;
    bbb -> bbb       [i.equals("check")] / o = "bbb";;
    ccc_c -> ccc_c   [i.equals("check")] / o = "ccc_c";;
    ddd_dd -> ddd_dd [i.equals("check")] / o = "ddd_dd";;

    // Transitions that should not be enabled in our test cases:
    a -> Target [i.equals("T")] / o = "a -> Target";;
    b -> Target [i.equals("T")] / o = "b -> Target";;
    c -> Target [i.equals("T")] / o = "c -> Target";;
    d -> Target [i.equals("T")] / o = "d -> Target";;

    aa -> Target [i.equals("T")] / o = "aa -> Target";;
    bb -> Target [i.equals("T")] / o = "bb -> Target";;
    cc -> Target [i.equals("T")] / o = "cc -> Target";;
    dd -> Target [i.equals("T")] / o = "dd -> Target";;

    bbb -> Target [i.equals("T")] / o = "bbb -> Target";;
    ccc -> Target [i.equals("T")] / o = "ccc -> Target";;
    ddd -> Target [i.equals("T")] / o = "ddd -> Target";;

    ccc_c -> Target [i.equals("T")] / o = "ccc_c -> Target";;
    ddd_d -> Target [i.equals("T")] / o = "ddd_d -> Target";;
    ddd_dd -> Target [i.equals("T")] / o = "ddd_dd -> Target";;
  }
}
