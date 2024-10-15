/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical;

/**
 * Checks the correct execution of transitions that come from within an hierarchical state.
 */
component NestedTransitionSources {

  port in String i;
  port out String o;

  <<sync>> automaton {

    // There are 6 mayor states: a, b, c, d, e, f
    // "f" has a sub state hierarchy of 5 additional levels,
    //     reflected by progressively longer names: ff, fff, fff_f, fff_ff, fff_fff
    // "e" only has 4 levels, "d" only 3, etc.
    // Every state in every hierarchy has an alterantive,
    //     abreviated with "z" at the level position of the name (e.g. "fff_z").
    //     These states always have sub states up to the 5th level: "fff_zzz"

    INIT -> a [i.equals("a")] / o = "-> a";;
    INIT -> b [i.equals("b")] / o = "-> b";;
    INIT -> c [i.equals("c")] / o = "-> c";;
    INIT -> d [i.equals("d")] / o = "-> d";;
    INIT -> e [i.equals("e")] / o = "-> e";;
    INIT -> f [i.equals("f")] / o = "-> f";;
    INIT -> Neutral [i.equals("Neutral")] / o = "-> Neutral";;

    // Tick-triggered transitions for checking that we are in the right state
    fff_fff -> fff_fff [i.equals("check")] / o = "fff_fff";;
    eee_ee -> eee_ee   [i.equals("check")] / o = "eee_ee";;
    ddd_d -> ddd_d     [i.equals("check")] / o = "ddd_d";;
    ccc -> ccc         [i.equals("check")] / o = "ccc";;
    bb -> bb           [i.equals("check")] / o = "bb";;
    a -> a             [i.equals("check")] / o = "a";;
    INIT -> INIT       [i.equals("check")] / o = "INIT";;
    Neutral -> Neutral [i.equals("check")] / o = "Neutral";;

    // Transitions that we want to check:
    // * Oneself or a super state is the transition source
    a -> Neutral [i.equals("a -> N")] / o = "a -> N";;
    b -> Neutral [i.equals("b -> N")] / o = "b -> N";;
    c -> Neutral [i.equals("c -> N")] / o = "c -> N";;
    d -> Neutral [i.equals("d -> N")] / o = "d -> N";;

    bb -> Neutral [i.equals("bb -> N")] / o = "bb -> N";;

    ccc -> Neutral [i.equals("ccc -> N")] / o = "ccc -> N";;
    ddd -> Neutral [i.equals("ddd -> N")] / o = "ddd -> N";;
    eee -> Neutral [i.equals("eee -> N")] / o = "eee -> N";;
    fff -> Neutral [i.equals("fff -> N")] / o = "fff -> N";;



    // States:
    initial state INIT;
    state Neutral;

    state a;
    
    // "b" goes down to level 1 (starting at 0)
    state b {
      initial state bb;
      state bz { initial state bzz { initial state bzz_z { initial state bzz_zz { initial state bzz_zzz; }; }; }; };
    };
        
    // "c" goes down to level 2 (starting at 0)
    state c {
      initial state cc {
        initial state ccc;
        state ccz { initial state ccz_z { initial state ccz_zz { initial state ccz_zzz; }; }; };
      };
      state cz { initial state czz { initial state czz_z { initial state czz_zz { initial state czz_zzz; }; }; }; };
    };
            
    // "d" goes down to level 3 (starting at 0)
    state d {
      initial state dd {
        initial state ddd {
          initial state ddd_d;
          state ddd_z { initial state ddd_zz { initial state ddd_zzz; }; };
        };
        state ddz { initial state ddz_z { initial state ddz_zz { initial state ddz_zzz; }; }; };
      };
      state dz { initial state dzz { initial state dzz_z { initial state dzz_zz {initial state dzz_zzz; }; }; }; };
    };
    
    // "e" goes down to level 4 (starting at 0)
    state e {
      initial state ee {
        initial state eee {
          initial state eee_e {
            initial state eee_ee;
            state eee_ez { initial state eee_ezz; };
          };
          state eee_z { initial state eee_zz { initial state eee_zzz; }; };
        };
        state eez { initial state eez_z { initial state eez_zz { initial state eez_zzz; }; }; };
      };
      state ez { initial state ezz { initial state ezz_z { initial state ezz_zz { initial state ezz_zzz; }; }; }; };
    };
        
    // "f" goes down to level 5 (starting at 0)
    state f {
      initial state ff {
        initial state fff {
          initial state fff_f {
            initial state fff_ff {
              initial state fff_fff;
              state fff_ffz;
            };
            state fff_fz { initial state fff_fzz; };
          };
          state fff_z { initial state fff_zz { initial state fff_zzz; }; };
        };
        state ffz { initial state ffz_z { initial state ffz_zz { initial state ffz_zzz; }; }; };
      };
      state fz { initial state fzz { initial state fzz_z { initial state fzz_zz { initial state fzz_zzz; }; }; }; };
    };

    state z {
      initial state zz {
        initial state zzz {
          initial state zzz_z {
            initial state zzz_zz {
              initial state zzz_zzz;
            };
          };
        };
      };
    };

  }

}
