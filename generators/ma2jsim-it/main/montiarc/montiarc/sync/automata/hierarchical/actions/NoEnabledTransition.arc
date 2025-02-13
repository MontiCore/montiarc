/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical.actions;

component NoEnabledTransition {

  port sync in String i;
  port sync out String o;

  automaton {

    initial state INIT;
    state Target;

    state a {
      entry / o = "-> a";   do / o = "~ a";   exit / o = "a ->";
      initial state aa {
        entry / o = "-> aa";   do / o = "~ aa";   exit / o = "aa ->";
      };
    };
    
    state b {
      entry / o = "-> b";   do / o = "~ b";   exit / o = "b ->";
      initial state bb {
        entry / o = "-> bb";   do / o = "~ bb";   exit / o = "bb ->";
        initial state bbb {
          entry / o = "-> bbb";   do / o = "~ bbb";   exit / o = "bbb ->";
        };
      };
    };
    
    state c {
      entry / o = "-> c";   do / o = "~ c";   exit / o = "c ->";
      initial state cc {
        entry / o = "-> cc";   do / o = "~ cc";   exit / o = "cc ->";
        initial state ccc {
          entry / o = "-> ccc";   do / o = "~ ccc";   exit / o = "ccc ->";
          initial state ccc_c {
            entry / o = "-> ccc_c";   do / o = "~ ccc_c";   exit / o = "ccc_c ->";
          };
        };
      };
    };
    
    state d {
      entry / o = "-> d";   do / o = "~ d";   exit / o = "d ->";
      initial state dd {
        entry / o = "-> dd";   do / o = "~ dd";   exit / o = "dd ->";
        initial state ddd {
          entry / o = "-> ddd";   do / o = "~ ddd";   exit / o = "ddd ->";
          initial state ddd_d {
            entry / o = "-> ddd_d";   do / o = "~ ddd_d";   exit / o = "ddd_d ->";
            initial state ddd_dd {
              entry / o = "-> ddd_dd";   do / o = "~ ddd_dd";   exit / o = "ddd_dd ->";
            };
          };
        };
      };
    };

    INIT -> aa     [i.equals("aa")]     / o = "INIT -> aa";;
    INIT -> bbb    [i.equals("bbb")]    / o = "INIT -> bbb";;
    INIT -> ccc_c  [i.equals("ccc_c")]  / o = "INIT -> ccc_c";;
    INIT -> ddd_dd [i.equals("ddd_dd")] / o = "INIT -> ddd_dd";;

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
