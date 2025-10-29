/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical.actions;

/**
 * Checks the correct execution of transitions that come from within an hierarchical state.
 */
component NestedTransitionsInSameHierarchy {

  port sync in String i;
  port sync out String o;

  automaton {

    // There are 6 mayor states: a, b, c, d, e, f
    // "f" has a sub state hierarchy of 5 additional levels,
    //     reflected by progressively longer names: ff, fff, fff_f, fff_ff, fff_fff
    // "e" only has 4 levels, "d" only 3, etc.
    // Every state in every hierarchy has an alterantive,
    //     abreviated with "z" at the level position of the name (e.g. "fff_z").
    //     These states always have sub states up to the 5th level: "fff_zzz"

    INIT -> a [i.equals("a")] / o = "INIT -> a";
    INIT -> b [i.equals("b")] / o = "INIT -> b";
    INIT -> c [i.equals("c")] / o = "INIT -> c";
    INIT -> d [i.equals("d")] / o = "INIT -> d";
    INIT -> e [i.equals("e")] / o = "INIT -> e";
    INIT -> f [i.equals("f")] / o = "INIT -> f";
    INIT -> Neutral [i.equals("N")] / o = "INIT -> N";



    // Transitions that we want to check:
    // * Moving up the hierarchy
    ccc -> c  [i.equals("ccc -> c")]  / o = "ccc -> c";
    ccc -> cc [i.equals("ccc -> cc")] / o = "ccc -> cc";

    ddd -> d  [i.equals("ddd -> d")]  / o = "ddd -> d";
    ddd -> dd [i.equals("ddd -> dd")] / o = "ddd -> dd";

    fff -> f          [i.equals("fff -> f")]          / o = "fff -> f";
    fff -> ff         [i.equals("fff -> ff")]         / o = "fff -> ff";
    fff_ff -> fff     [i.equals("fff_ff -> fff")]     / o = "fff_ff -> fff";
    fff_ff -> fff_f   [i.equals("fff_ff -> fff_f")]   / o = "fff_ff -> fff_f";
    fff_fff -> fff_f  [i.equals("fff_fff -> fff_f")]  / o = "fff_fff -> fff_f";
    fff_fff -> fff_ff [i.equals("fff_fff -> fff_ff")] / o = "fff_fff -> fff_ff";

    // * Moving "down" the hierarchy
    b -> bz  [i.equals("b -> bz")]  / o = "b -> bz";
    c -> cz  [i.equals("c -> cz")]  / o = "c -> cz";
    c -> czz [i.equals("c -> czz")] / o = "c -> czz";

    d -> dz   [i.equals("d -> dz")]    / o = "d -> dz";
    d -> dzz  [i.equals("d -> dzz")]   / o = "d -> dzz";
    dd -> ddz [i.equals("dd -> ddz")]  / o = "dd -> ddz";

    f -> ffz        [i.equals("f -> ffz")]        / o = "f -> ffz";
    f -> fzz_z      [i.equals("f -> fzz_z")]      / o = "f -> fzz_z";
    fff -> fff_zz   [i.equals("fff -> fff_zz")]   / o = "fff -> fff_zz";
    fff -> fff_zzz  [i.equals("fff -> fff_zzz")]  / o = "fff -> fff_zzz";
    fff_f -> fff_fz [i.equals("fff_f -> fff_fz")] / o = "fff_f -> fff_fz";

    // * Staying at the same hierarchical level
    bb -> bz [i.equals("bb -> bz")] / o = "bb -> bz";
    cc -> cz [i.equals("cc -> cz")] / o = "cc -> cz";
    dd -> dz [i.equals("dd -> dz")] / o = "dd -> dz";
    ee -> ez [i.equals("ee -> ez")] / o = "ee -> ez";

    ccc -> ccz [i.equals("ccc -> ccz")] / o = "ccc -> ccz";
    ccc -> czz [i.equals("ccc -> czz")] / o = "ccc -> czz";
    fff -> fzz [i.equals("fff -> fzz")] / o = "fff -> fzz";
    fff_fff -> fff_ffz [i.equals("fff_fff -> fff_ffz")] / o = "fff_fff -> fff_ffz";
    fff_fff -> fff_zzz [i.equals("fff_fff -> fff_zzz")] / o = "fff_fff -> fff_zzz";

    // * Staying at the same hierarchy level and in the same state
    b -> b [i.equals("b -> b")]                         / o = "b -> b";
    c -> c [i.equals("c -> c")]                         / o = "c -> c";
    cc -> cc [i.equals("cc -> cc")]                     / o = "cc -> cc";
    fff -> fff [i.equals("fff -> fff")]                 / o = "fff -> fff";
    fff_ff -> fff_ff [i.equals("fff_ff -> fff_ff")]     / o = "fff_ff -> fff_ff";
    fff_fff -> fff_fff [i.equals("fff_fff -> fff_fff")] / o = "fff_fff -> fff_fff";



    // States
    initial state INIT;
    state Neutral {
      entry / o = "-> N";   do / o = "~ N";   exit / o = "N ->";
    }

    state a {
      entry / o = "-> a";   do / o = "~ a";   exit / o = "a ->";
    }

    // "b" goes down to level 1 (starting at 0)
    state b {
      entry / o = "-> b";   do / o = "~ b";   exit / o = "b ->";
      initial state bb {
        entry / o = "-> bb";   do / o = "~ bb";   exit / o = "bb ->";
      }

      state bz {
        entry / o = "-> bz";   do / o = "~ bz";   exit / o = "bz ->";
        initial state bzz {
          entry / o = "-> bzz";   do / o = "~ bzz";   exit / o = "bzz ->";
          initial state bzz_z {
            entry / o = "-> bzz_z";   do / o = "~ bzz_z";   exit / o = "bzz_z ->";
            initial state bzz_zz {
              entry / o = "-> bzz_zz";   do / o = "~ bzz_zz";   exit / o = "bzz_zz ->";
              initial state bzz_zzz {
                entry / o = "-> bzz_zzz";   do / o = "~ bzz_zzz";   exit / o = "bzz_zzz ->";
      }}}}}}

    // "c" goes down to level 2 (starting at 0)
    state c {
      entry / o = "-> c";   do / o = "~ c";   exit / o = "c ->";
      initial state cc {
        entry / o = "-> cc";   do / o = "~ cc";   exit / o = "cc ->";
        initial state ccc {
          entry / o = "-> ccc";   do / o = "~ ccc";   exit / o = "ccc ->";
        }

        state ccz {
          entry / o = "-> ccz";   do / o = "~ ccz";   exit / o = "ccz ->";
          initial state ccz_z {
            entry / o = "-> ccz_z";   do / o = "~ ccz_z";   exit / o = "ccz_z ->";
            initial state ccz_zz {
              entry / o = "-> ccz_zz";   do / o = "~ ccz_zz";   exit / o = "ccz_zz ->";
              initial state ccz_zzz {
                entry / o = "-> ccz_zzz";   do / o = "~ ccz_zzz";   exit / o = "ccz_zzz ->";
        }}}}}

      state cz {
        entry / o = "-> cz";   do / o = "~ cz";   exit / o = "cz ->";
        initial state czz {
          entry / o = "-> czz";   do / o = "~ czz";   exit / o = "czz ->";
          initial state czz_z {
            entry / o = "-> czz_z";   do / o = "~ czz_z";   exit / o = "czz_z ->";
            initial state czz_zz {
              entry / o = "-> czz_zz";   do / o = "~ czz_zz";   exit / o = "czz_zz ->";
              initial state czz_zzz {
                entry / o = "-> czz_zzz";   do / o = "~ czz_zzz";   exit / o = "czz_zzz ->";
      }}}}}
    }

    // "d" goes down to level 3 (starting at 0)
    state d {
      entry / o = "-> d";   do / o = "~ d";   exit / o = "d ->";
      initial state dd {
        entry / o = "-> dd";   do / o = "~ dd";   exit / o = "dd ->";
        initial state ddd {
          entry / o = "-> ddd";   do / o = "~ ddd";   exit / o = "ddd ->";
          initial state ddd_d {
            entry / o = "-> ddd_d";   do / o = "~ ddd_d";   exit / o = "ddd_d ->";
          }

          state ddd_z {
            initial state ddd_zz {
              initial state ddd_zzz {
                entry / o = "-> ddd_zzz";   do / o = "~ ddd_zzz";   exit / o = "ddd_zzz ->";
          }}}
        }

        state ddz {
          entry / o = "-> ddz";   do / o = "~ ddz";   exit / o = "ddz ->";
          initial state ddz_z {
            entry / o = "-> ddz_z";   do / o = "~ ddz_z";   exit / o = "ddz_z ->";
            initial state ddz_zz {
              entry / o = "-> ddz_zz";   do / o = "~ ddz_zz";   exit / o = "ddz_zz ->";
              initial state ddz_zzz {
                entry / o = "-> ddz_zzz";   do / o = "~ ddz_zzz";   exit / o = "ddz_zzz ->";
        }}}}
      }

      state dz {
        entry / o = "-> dz";   do / o = "~ dz";   exit / o = "dz ->";
        initial state dzz {
          entry / o = "-> dzz";   do / o = "~ dzz";   exit / o = "dzz ->";
          initial state dzz_z {
            entry / o = "-> dzz_z";   do / o = "~ dzz_z";   exit / o = "dzz_z ->";
            initial state dzz_zz {
              entry / o = "-> dzz_zz";   do / o = "~ dzz_zz";   exit / o = "dzz_zz ->";
              initial state dzz_zzz {
                entry / o = "-> dzz_zzz";   do / o = "~ dzz_zzz";   exit / o = "dzz_zzz ->";
      }}}}}
    }

    // "e" goes down to level 4 (starting at 0)
    state e {
      entry / o = "-> e";   do / o = "~ e";   exit / o = "e ->";
      initial state ee {
        entry / o = "-> ee";   do / o = "~ ee";   exit / o = "ee ->";
        initial state eee {
          entry / o = "-> eee";   do / o = "~ eee";   exit / o = "eee ->";
          initial state eee_e {
            entry / o = "-> eee_e";   do / o = "~ eee_e";   exit / o = "eee_e ->";
            initial state eee_ee {
              entry / o = "-> eee_ee";   do / o = "~ eee_ee";   exit / o = "eee_ee ->";
            }

            state eee_ez {
              entry / o = "-> eee_ez";   do / o = "~ eee_ez";   exit / o = "eee_ez ->";
              initial state eee_ezz {
                entry / o = "-> eee_ezz";   do / o = "~ eee_ezz";   exit / o = "eee_ezz ->";
            }}
          }

          state eee_z {
            entry / o = "-> eee_z";   do / o = "~ eee_z";   exit / o = "eee_z ->";
            initial state eee_zz {
              entry / o = "-> eee_zz";   do / o = "~ eee_zz";   exit / o = "eee_zz ->";
              initial state eee_zzz {
                entry / o = "-> eee_zzz";   do / o = "~ eee_zzz";   exit / o = "eee_zzz ->";
          }}}
        }

        state eez {
          entry / o = "-> eez";   do / o = "~ eez";   exit / o = "eez ->";
          initial state eez_z {
            entry / o = "-> eez_z";   do / o = "~ eez_z";   exit / o = "eez_z ->";
            initial state eez_zz {
              entry / o = "-> eez_zz";   do / o = "~ eez_zz";   exit / o = "eez_zz ->";
              initial state eez_zzz {
                entry / o = "-> eez_zzz";   do / o = "~ eez_zzz";   exit / o = "eez_zzz ->";
        }}}}
      }

      state ez {
        entry / o = "-> ez";   do / o = "~ ez";   exit / o = "ez ->";
        initial state ezz {
          entry / o = "-> ezz";   do / o = "~ ezz";   exit / o = "ezz ->";
          initial state ezz_z {
            entry / o = "-> ezz_z";   do / o = "~ ezz_z";   exit / o = "ezz_z ->";
            initial state ezz_zz {
              entry / o = "-> ezz_zz";   do / o = "~ ezz_zz";   exit / o = "ezz_zz ->";
              initial state ezz_zzz {
                entry / o = "-> ezz_zzz";   do / o = "~ ezz_zzz";   exit / o = "ezz_zzz ->";
      }}}}}
    }

    // "f" goes down to level 5 (starting at 0)
    state f {
      entry / o = "-> f";   do / o = "~ f";   exit / o = "f ->";
      initial state ff {
        entry / o = "-> ff";   do / o = "~ ff";   exit / o = "ff ->";
        initial state fff {
          entry / o = "-> fff";   do / o = "~ fff";   exit / o = "fff ->";
          initial state fff_f {
            entry / o = "-> fff_f";   do / o = "~ fff_f";   exit / o = "fff_f ->";
            initial state fff_ff {
              entry / o = "-> fff_ff";   do / o = "~ fff_ff";   exit / o = "fff_ff ->";
              initial state fff_fff {
                entry / o = "-> fff_fff";   do / o = "~ fff_fff";   exit / o = "fff_fff ->";
              }

              state fff_ffz {
                entry / o = "-> fff_ffz";   do / o = "~ fff_ffz";   exit / o = "fff_ffz ->";
              }
            }

            state fff_fz {
              entry / o = "-> fff_fz";   do / o = "~ fff_fz";   exit / o = "fff_fz ->";
              initial state fff_fzz {
              entry / o = "-> fff_fzz";   do / o = "~ fff_fzz";   exit / o = "fff_fzz ->";
            }}
          }

          state fff_z {
            entry / o = "-> fff_z";   do / o = "~ fff_z";   exit / o = "fff_z ->";
            initial state fff_zz {
              entry / o = "-> fff_zz";   do / o = "~ fff_zz";   exit / o = "fff_zz ->";
              initial state fff_zzz {
                entry / o = "-> fff_zzz";   do / o = "~ fff_zzz";   exit / o = "fff_zzz ->";
          }}}
        }

        state ffz {
          entry / o = "-> ffz";   do / o = "~ ffz";   exit / o = "ffz ->";
          initial state ffz_z {
            entry / o = "-> ffz_z";   do / o = "~ ffz_z";   exit / o = "ffz_z ->";
            initial state ffz_zz {
              entry / o = "-> ffz_zz";   do / o = "~ ffz_zz";   exit / o = "ffz_zz ->";
              initial state ffz_zzz {
                entry / o = "-> ffz_zzz";   do / o = "~ ffz_zzz";   exit / o = "ffz_zzz ->";
        }}}}
      }

      state fz {
        entry / o = "-> fz";   do / o = "~ fz";   exit / o = "fz ->";
        initial state fzz {
          entry / o = "-> fzz";   do / o = "~ fzz";   exit / o = "fzz ->";
          initial state fzz_z {
            entry / o = "-> fzz_z";   do / o = "~ fzz_z";   exit / o = "fzz_z ->";
            initial state fzz_zz {
              entry / o = "-> fzz_zz";   do / o = "~ fzz_zz";   exit / o = "fzz_zz ->";
              initial state fzz_zzz {
                entry / o = "-> fzz_zzz";   do / o = "~ fzz_zzz";   exit / o = "fzz_zzz ->";
      }}}}}
    }

    state z {
      entry / o = "-> z";   do / o = "~ z";   exit / o = "z ->";
      initial state zz {
        entry / o = "-> zz";   do / o = "~ zz";   exit / o = "zz ->";
        initial state zzz {
          entry / o = "-> zzz";   do / o = "~ zzz";   exit / o = "zzz ->";
          initial state zzz_z {
            entry / o = "-> zzz_z";   do / o = "~ zzz_z";   exit / o = "zzz_z ->";
            initial state zzz_zz {
              entry / o = "-> zzz_zz";   do / o = "~ zzz_zz";   exit / o = "zzz_zz ->";
              initial state zzz_zzz {
                entry / o = "-> zzz_zzz";   do / o = "~ zzz_zzz";   exit / o = "zzz_zzz ->";
              }
            }
          }
        }
      }
    }

  }

}
