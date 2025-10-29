/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical.actions;

/**
 * Checks the correct execution of transitions that come from within an hierarchical state.
 *
 * Test cases:
 * 1. Transition leaves one hierarchical state completely and enters another one.
 * 2. Transition ascends to the root of a hierarchical state and descends from there.
 * 3. Transition ascends to a sub state (1st level) and descends from there.
 * 4. Transition ascends to a sub sub state (2nd level) and descends from there.
 *
 *  5./ 6./ 7. Transition starts at leaf and ascends 1 / 2 / 3 steps before descending.
 *  8./ 9./10. Transition starts at leaf parent and ascends 1 / 2 / 3 steps before descending.
 * 11./12./13. Transition starts at leaf grandparent and ascends 1 / 2 / 3 steps before descending.
 * 14./15./16. After ascending, the transition descends 1 / 2 / 3 steps to reach a leaf.
 * 17./18./19. After ascending, the transition descends 1 / 2 / 3steps to reach a leaf parent.
 * 20./21./22. After ascending, the transition descends 1 / 2 / 3steps to reach a leaf grandparent.
 */
component NestedTransitionsCrosscuttingTheHierarchy {

  port sync in String i;
  port sync out String o;

  automaton {
    initial state INIT;

    // There are various bystanders that in the hierarchy that offer alternative paths,
    // but should not be entered.
    state bystander;

    // The names of a state in the hierarchy is a sequence of letters.
    // The letter identifies the sibling in the hierarchy: a is the first sibling and b the second (if existent).
    // Example: aaa_ba has the following ancestors: aaa_b, aaa, aa, a (moving up the state hierarchy to the root)

    // Transitions to the initial state of every test case:
    INIT -> aaa_aaa [i.equals("aaa_aaa")] / o = "INIT -> aaa_aaa";

    // Test case transitions:
    aaa_aaa -> aaa_aab [i.equals("aaa_aaa -> aaa_aab")] / o = "aaa_aaa -> aaa_aab";  // 5., 14.
    aaa_aaa -> aaa_aba [i.equals("aaa_aaa -> aaa_aba")] / o = "aaa_aaa -> aaa_aba";  // 6., 15.
    aaa_aaa -> aaa_baa [i.equals("aaa_aaa -> aaa_baa")] / o = "aaa_aaa -> aaa_baa";  // 7., 16.
    aaa_aa -> aaa_ab   [i.equals("aaa_aa -> aaa_ab")]   / o = "aaa_aa -> aaa_ab";    // 8., 17.
    aaa_aa -> aaa_ba   [i.equals("aaa_aa -> aaa_ba")]   / o = "aaa_aa -> aaa_ba";    // 9., 18.
    aaa_aa -> aab_aa   [i.equals("aaa_aa -> aab_aa")]   / o = "aaa_aa -> aab_aa";    // 10., 19.
    aaa_a -> aaa_b     [i.equals("aaa_a -> aaa_b")]     / o = "aaa_a -> aaa_b";  // 11., 20., 4.
    aaa_a -> aab_a     [i.equals("aaa_a -> aab_a")]     / o = "aaa_a -> aab_a";  // 12., 21., 3.
    aaa_a -> aba_a     [i.equals("aaa_a -> aba_a")]     / o = "aaa_a -> aba_a";  // 13., 22., 2.
    aa -> baa          [i.equals("aa -> baa")]          / o = "aa -> baa";  // 1.


    // States:
    state a { entry / o = "-> a";   do / o = "~ a";   exit / o = "a ->";
      initial state a_bystander { entry / o = "-> a_by";   do / o = "~ a_by";   exit / o = "a_by ->"; }
      state aa { entry / o = "-> aa";   do / o = "~ aa";   exit / o = "aa ->";
        initial state aa_bystander { entry / o = "-> aa_by";   do / o = "~ aa_by";   exit / o = "aa_by ->"; }
        state aaa { entry / o = "-> aaa";   do / o = "~ aaa";   exit / o = "aaa ->";
          initial state aaa_bystander { entry / o = "-> aaa_by";   do / o = "~ aaa_by";   exit / o = "aaa_by ->"; }
          state aaa_a { entry / o = "-> aaa_a";   do / o = "~ aaa_a";   exit / o = "aaa_a ->";
            initial state aaa_a_bystander { entry / o = "-> aaa_a_by";   do / o = "~ aaa_a_by";   exit / o = "aaa_a_by ->"; }
            state aaa_aa { entry / o = "-> aaa_aa";   do / o = "~ aaa_aa";   exit / o = "aaa_aa ->";
              initial state aaa_aa_bystander { entry / o = "-> aaa_aa_by";   do / o = "~ aaa_aa_by";   exit / o = "aaa_aa_by ->"; }
              state aaa_aaa { entry / o = "-> aaa_aaa";   do / o = "~ aaa_aaa";   exit / o = "aaa_aaa ->"; }
              state aaa_aab { entry / o = "-> aaa_aab";   do / o = "~ aaa_aab";   exit / o = "aaa_aab ->"; }
            }

            state aaa_ab { entry / o = "-> aaa_ab";   do / o = "~ aaa_ab";   exit / o = "aaa_ab ->";
              state aaa_aba { entry / o = "-> aaa_aba";   do / o = "~ aaa_aba";   exit / o = "aaa_aba ->"; }
              initial state aaa_abb { entry / o = "-> aaa_abb";   do / o = "~ aaa_abb";   exit / o = "aaa_abb ->"; }
            }
          }

          state aaa_b { entry / o = "-> aaa_b";   do / o = "~ aaa_b";   exit / o = "aaa_b ->";
            state aaa_ba { entry / o = "-> aaa_ba";   do / o = "~ aaa_ba";   exit / o = "aaa_ba ->";
              state aaa_baa { entry / o = "-> aaa_baa";   do / o = "~ aaa_baa";   exit / o = "aaa_baa ->"; }
              initial state aaa_bab { entry / o = "-> aaa_bab";   do / o = "~ aaa_bab";   exit / o = "aaa_bab ->"; }
            }
            initial state aaa_bb { entry / o = "-> aaa_bb";   do / o = "~ aaa_bb";   exit / o = "aaa_bb ->";
              initial state aaa_bba { entry / o = "-> aaa_bba";   do / o = "~ aaa_bba";   exit / o = "aaa_bba ->"; }
            }
          }
        }

        state aab { entry / o = "-> aab";   do / o = "~ aab";   exit / o = "aab ->";
          initial state aab_bystander { entry / o = "-> aab_by";   do / o = "~ aab_by";   exit / o = "aab_by ->"; }
          state aab_a { entry / o = "-> aab_a";   do / o = "~ aab_a";   exit / o = "aab_a ->";
            state aab_aa { entry / o = "-> aab_aa";   do / o = "~ aab_aa";   exit / o = "aab_aa ->";
              initial state aab_aaa { entry / o = "-> aab_aaa";   do / o = "~ aab_aaa";   exit / o = "aab_aaa ->"; }
            }
            initial state aab_ab { entry / o = "-> aab_ab";   do / o = "~ aab_ab";   exit / o = "aab_ab ->";
              initial state aab_aba { entry / o = "-> aab_aba";   do / o = "~ aab_aba";   exit / o = "aab_aba ->"; }
            }
          }
        }
      }

      state ab { entry / o = "-> ab";   do / o = "~ ab";   exit / o = "ab ->";
        initial state ab_bystander { entry / o = "-> ab_by";   do / o = "~ ab_by";   exit / o = "ab_by ->"; }
        state aba { entry / o = "-> aba";   do / o = "~ aba";   exit / o = "aba ->";
          initial state aba_bystander { entry / o = "-> aba_by";   do / o = "~ aba_by";   exit / o = "aba_by ->"; }
          state aba_a { entry / o = "-> aba_a";   do / o = "~ aba_a";   exit / o = "aba_a ->";
            initial state aba_aa { entry / o = "-> aba_aa";   do / o = "~ aba_aa";   exit / o = "aba_aa ->";
              initial state aba_aaa { entry / o = "-> aba_aaa";   do / o = "~ aba_aaa";   exit / o = "aba_aaa ->"; }
            }
          }
        }
      }
    }

    state b { entry / o = "-> b";   do / o = "~ b";   exit / o = "b ->";
      initial state b_bystander { entry / o = "-> b_by";   do / o = "~ b_by";   exit / o = "b_by ->"; }
      state ba { entry / o = "-> ba";   do / o = "~ ba";   exit / o = "ba ->";
        initial state ba_bystander { entry / o = "-> ba_by";   do / o = "~ ba_by";   exit / o = "ba_by ->"; }
        state baa { entry / o = "-> baa";   do / o = "~ baa";   exit / o = "baa ->"; }
      }
    }
  }
}
