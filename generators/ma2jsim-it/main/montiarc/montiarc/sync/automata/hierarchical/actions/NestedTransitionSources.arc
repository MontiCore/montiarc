/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical.actions;

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

    INIT -> a [i.equals("a")] / o = "INIT -> a";;
    INIT -> b [i.equals("b")] / o = "INIT -> b";;
    INIT -> c [i.equals("c")] / o = "INIT -> c";;
    INIT -> d [i.equals("d")] / o = "INIT -> d";;
    INIT -> e [i.equals("e")] / o = "INIT -> e";;
    INIT -> f [i.equals("f")] / o = "INIT -> f";;
    INIT -> Neutral [i.equals("Neutral")] / o = "INIT -> Neutral";;

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
    state Neutral {
      entry / o = "-> N";   do / o = "~ N";   exit / o = "N ->";
    };

    state a {
      entry / o = "-> a";   do / o = "~ a";   exit / o = "a ->";
    };
    
    // "b" goes down to level 1 (starting at 0)
    state b {
      entry / o = "-> b";   do / o = "~ b";   exit / o = "b ->";
      initial state bb {
        entry / o = "-> bb";   do / o = "~ bb";   exit / o = "bb ->";
      };
      state bz { initial state bzz { initial state bzz_z { initial state bzz_zz { initial state bzz_zzz; }; }; }; };
    };
        
    // "c" goes down to level 2 (starting at 0)
    state c {
      entry / o = "-> c";   do / o = "~ c";   exit / o = "c ->";
      initial state cc {
      entry / o = "-> cc";   do / o = "~ cc";   exit / o = "cc ->";
        initial state ccc {
          entry / o = "-> ccc";   do / o = "~ ccc";   exit / o = "ccc ->";
        };
        state ccz { initial state ccz_z { initial state ccz_zz { initial state ccz_zzz; }; }; };
      };
      state cz { initial state czz { initial state czz_z { initial state czz_zz { initial state czz_zzz; }; }; }; };
    };
            
    // "d" goes down to level 3 (starting at 0)
    state d {
      entry / o = "-> d";   do / o = "~ d";   exit / o = "d ->";
      initial state dd {
        entry / o = "-> dd";   do / o = "~ dd";   exit / o = "dd ->";
        initial state ddd {
          entry / o = "-> ddd";   do / o = "~ ddd";   exit / o = "ddd ->";
          initial state ddd_d {
            entry / o = "-> ddd_d";   do / o = "~ ddd_d";   exit / o = "ddd_d ->";
          };
          state ddd_z { initial state ddd_zz { initial state ddd_zzz; }; };
        };
        state ddz { initial state ddz_z { initial state ddz_zz { initial state ddz_zzz; }; }; };
      };
      state dz { initial state dzz { initial state dzz_z { initial state dzz_zz {initial state dzz_zzz; }; }; }; };
    };
    
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
            };
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
              };
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
