/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical;

/**
 * Checks the correct execution of transitions that come from within an hierarchical state.
 */
component NestedTransitionsInSameHierarchy {

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

    bz -> bz           [i.equals("check")] / o = "bz";;
    ccz -> ccz         [i.equals("check")] / o = "ccz";;
    czz -> czz         [i.equals("check")] / o = "czz";;
    ddd_z -> ddd_z     [i.equals("check")] / o = "ddd_z";;
    ddz_z -> ddz_z     [i.equals("check")] / o = "ddz_z";;
    dzz_z -> dzz_z     [i.equals("check")] / o = "dzz_z";;
    eee_ez -> eee_ez   [i.equals("check")] / o = "eee_ez";;
    eee_zz -> eee_zz   [i.equals("check")] / o = "eee_zz";;
    eez_zz -> eez_zz   [i.equals("check")] / o = "eez_zz";;
    ezz_zz -> ezz_zz   [i.equals("check")] / o = "ezz_zz";;
    fff_ffz -> fff_ffz [i.equals("check")] / o = "fff_ffz";;
    fff_fzz -> fff_fzz [i.equals("check")] / o = "fff_fzz";;
    fff_zzz -> fff_zzz [i.equals("check")] / o = "fff_zzz";;
    ffz_zzz -> ffz_zzz [i.equals("check")] / o = "ffz_zzz";;
    fzz_zzz -> fzz_zzz [i.equals("check")] / o = "fzz_zzz";;



    // Transitions that we want to check:
    // * Moving up the hierarchy
    ccc -> c  [i.equals("ccc -> c")]  / o = "ccc -> c";;
    ccc -> cc [i.equals("ccc -> cc")] / o = "ccc -> cc";;

    ddd -> d  [i.equals("ddd -> d")]  / o = "ddd -> d";;
    ddd -> dd [i.equals("ddd -> dd")] / o = "ddd -> dd";;

    fff -> f          [i.equals("fff -> f")]          / o = "fff -> f";;
    fff -> ff         [i.equals("fff -> ff")]         / o = "fff -> ff";;
    fff_ff -> fff     [i.equals("fff_ff -> fff")]     / o = "fff_ff -> fff";;
    fff_ff -> fff_f   [i.equals("fff_ff -> fff_f")]   / o = "fff_ff -> fff_f";;
    fff_fff -> fff_f  [i.equals("fff_fff -> fff_f")]  / o = "fff_fff -> fff_f";;
    fff_fff -> fff_ff [i.equals("fff_fff -> fff_ff")] / o = "fff_fff -> fff_ff";;

    // * Moving "down" the hierarchy
    b -> bz  [i.equals("b -> bz")]  / o = "b -> bz";;
    c -> cz  [i.equals("c -> cz")]  / o = "c -> cz";;
    c -> czz [i.equals("c -> czz")] / o = "c -> czz";;

    d -> dz   [i.equals("d -> dz")]    / o = "d -> dz";;    dzz_z -> dzz_z / o = "dzz_z";;
    d -> dzz  [i.equals("d -> dzz")]   / o = "d -> dzz";;
    dd -> ddz [i.equals("dd -> ddz")]  / o = "dd -> ddz";;  ddz_z -> ddz_z / o = "ddz_z";;

    f -> ffz        [i.equals("f -> ffz")]        / o = "f -> ffz";;
    f -> fzz_z      [i.equals("f -> fzz_z")]      / o = "f -> fzz_z";;
    fff -> fff_zz   [i.equals("fff -> fff_zz")]   / o = "fff -> fff_zz";;
    fff -> fff_zzz  [i.equals("fff -> fff_zzz")]  / o = "fff -> fff_zzz";;
    fff_f -> fff_fz [i.equals("fff_f -> fff_fz")] / o = "fff_f -> fff_fz";;

    // * Staying at the same hierarchical level
    bb -> bz [i.equals("bb -> bz")] / o = "bb -> bz";;
    cc -> cz [i.equals("cc -> cz")] / o = "cc -> cz";;
    dd -> dz [i.equals("dd -> dz")] / o = "dd -> dz";;
    ee -> ez [i.equals("ee -> ez")] / o = "ee -> ez";;

    ccc -> ccz [i.equals("ccc -> ccz")] / o = "ccc -> ccz";;
    ccc -> czz [i.equals("ccc -> czz")] / o = "ccc -> czz";;
    fff -> fzz [i.equals("fff -> fzz")] / o = "fff -> fzz";;
    fff_fff -> fff_ffz [i.equals("fff_fff -> fff_ffz")] / o = "fff_fff -> fff_ffz";;
    fff_fff -> fff_zzz [i.equals("fff_fff -> fff_zzz")] / o = "fff_fff -> fff_zzz";;

    // * Staying at the same hierarchy level and in the same state
    b -> b [i.equals("b -> b")]                         / o = "b -> b";;
    c -> c [i.equals("c -> c")]                         / o = "c -> c";;
    cc -> cc [i.equals("cc -> cc")]                     / o = "cc -> cc";;
    fff -> fff [i.equals("fff -> fff")]                 / o = "fff -> fff";;
    fff_ff -> fff_ff [i.equals("fff_ff -> fff_ff")]     / o = "fff_ff -> fff_ff";;
    fff_fff -> fff_fff [i.equals("fff_fff -> fff_fff")] / o = "fff_fff -> fff_fff";;



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
