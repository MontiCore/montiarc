/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical.actions;

component NoEnabledTransition {

  port in String i;
  port out String o;

  automaton {

    initial state INIT;
    state Target;

    state a {
      entry / o = "-> a";   do / o = "~ a";   exit / o = "a ->";
      initial state aa {
        entry / o = "-> aa";   do / o = "~ aa";   exit / o = "aa ->";
      }
    }
    
    state b {
      entry / o = "-> b";   do / o = "~ b";   exit / o = "b ->";
      initial state bb {
        entry / o = "-> bb";   do / o = "~ bb";   exit / o = "bb ->";
        initial state bbb {
          entry / o = "-> bbb";   do / o = "~ bbb";   exit / o = "bbb ->";
        }
      }
    }
    
    state c {
      entry / o = "-> c";   do / o = "~ c";   exit / o = "c ->";
      initial state cc {
        entry / o = "-> cc";   do / o = "~ cc";   exit / o = "cc ->";
        initial state ccc {
          entry / o = "-> ccc";   do / o = "~ ccc";   exit / o = "ccc ->";
          initial state ccc_c {
            entry / o = "-> ccc_c";   do / o = "~ ccc_c";   exit / o = "ccc_c ->";
          }
        }
      }
    }
    
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
            }
          }
        }
      }
    }

    INIT -> aa     [i.equals("aa")]     i / o = "INIT -> aa";
    INIT -> bbb    [i.equals("bbb")]    i / o = "INIT -> bbb";
    INIT -> ccc_c  [i.equals("ccc_c")]  i / o = "INIT -> ccc_c";
    INIT -> ddd_dd [i.equals("ddd_dd")] i / o = "INIT -> ddd_dd";

    // Transitions that should not be enabled in our test cases:
    a -> Target [i.equals("T")] i / o = "a -> Target";
    b -> Target [i.equals("T")] i / o = "b -> Target";
    c -> Target [i.equals("T")] i / o = "c -> Target";
    d -> Target [i.equals("T")] i / o = "d -> Target";

    aa -> Target [i.equals("T")] i / o = "aa -> Target";
    bb -> Target [i.equals("T")] i / o = "bb -> Target";
    cc -> Target [i.equals("T")] i / o = "cc -> Target";
    dd -> Target [i.equals("T")] i / o = "dd -> Target";

    bbb -> Target [i.equals("T")] i / o = "bbb -> Target";
    ccc -> Target [i.equals("T")] i / o = "ccc -> Target";
    ddd -> Target [i.equals("T")] i / o = "ddd -> Target";

    ccc_c -> Target [i.equals("T")] i / o = "ccc_c -> Target";
    ddd_d -> Target [i.equals("T")] i / o = "ddd_d -> Target";
    ddd_dd -> Target [i.equals("T")] i / o = "ddd_dd -> Target";
  }
}
