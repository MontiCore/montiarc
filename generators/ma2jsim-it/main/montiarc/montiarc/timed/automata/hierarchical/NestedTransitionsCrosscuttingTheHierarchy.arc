/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical;

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

  port in String i;
  port out String o;

  <<timed>> automaton {
    initial state INIT;

    // There are various bystanders that in the hierarchy that offer alternative paths,
    // but should not be entered.
    state bystander;

    // The names of a state in the hierarchy is a sequence of letters.
    // The letter identifies the sibling in the hierarchy: a is the first sibling and b the second (if existent).
    // Example: aaa_ba has the following ancestors: aaa_b, aaa, aa, a (moving up the state hierarchy to the root)

    // Transitions for identifying the reached leaf state that the component is in at a given time:
    aaa_aaa -> aaa_aaa [i.equals("check")] i / o = "aaa_aaa";;
    aaa_aab -> aaa_aab [i.equals("check")] i / o = "aaa_aab";;
    aaa_aba -> aaa_aba [i.equals("check")] i / o = "aaa_aba";;
    aaa_abb -> aaa_abb [i.equals("check")] i / o = "aaa_abb";;
    aaa_baa -> aaa_baa [i.equals("check")] i / o = "aaa_baa";;
    aaa_bab -> aaa_bab [i.equals("check")] i / o = "aaa_bab";;
    aaa_bba -> aaa_bba [i.equals("check")] i / o = "aaa_bba";;
    aaa_bb -> aaa_bb   [i.equals("check")] i / o = "aaa_bb";;
    aab_aaa -> aab_aaa [i.equals("check")] i / o = "aab_aaa";;
    aab_aba -> aab_aba [i.equals("check")] i / o = "aab_aba";;
    aab_ab -> aab_ab   [i.equals("check")] i / o = "aab_ab";;
    aba_aaa -> aba_aaa [i.equals("check")] i / o = "aba_aaa";;
    baa -> baa         [i.equals("check")] i / o = "baa";;

    // Transitions to the initial state of every test case:
    INIT -> aaa_aaa [i.equals("aaa_aaa")] i / o = "-> aaa_aaa";;

    // Test case transitions:
    aaa_aaa -> aaa_aab [i.equals("aaa_aaa -> aaa_aab")] i / o = "aaa_aaa -> aaa_aab";;  // 5., 14.
    aaa_aaa -> aaa_aba [i.equals("aaa_aaa -> aaa_aba")] i / o = "aaa_aaa -> aaa_aba";;  // 6., 15.
    aaa_aaa -> aaa_baa [i.equals("aaa_aaa -> aaa_baa")] i / o = "aaa_aaa -> aaa_baa";;  // 7., 16.
    aaa_aa -> aaa_ab   [i.equals("aaa_aa -> aaa_ab")]   i / o = "aaa_aa -> aaa_ab";;    // 8., 17.
    aaa_aa -> aaa_ba   [i.equals("aaa_aa -> aaa_ba")]   i / o = "aaa_aa -> aaa_ba";;    // 9., 18.
    aaa_aa -> aab_aa   [i.equals("aaa_aa -> aab_aa")]   i / o = "aaa_aa -> aab_aa";;    // 10., 19.
    aaa_a -> aaa_b     [i.equals("aaa_a -> aaa_b")]     i / o = "aaa_a -> aaa_b";;  // 11., 20., 4.
    aaa_a -> aab_a     [i.equals("aaa_a -> aab_a")]     i / o = "aaa_a -> aab_a";;  // 12., 21., 3.
    aaa_a -> aba_a     [i.equals("aaa_a -> aba_a")]     i / o = "aaa_a -> aba_a";;  // 13., 22., 2.
    aa -> baa          [i.equals("aa -> baa")]          i / o = "aa -> baa";;  // 1.


    // States:
    state a {
      initial state a_bystander;
      state aa {
        initial state aa_bystander;
        state aaa {
          initial state aaa_bystander;
          state aaa_a {
            initial state aaa_a_bystander;
            state aaa_aa {
              initial state aaa_aa_bystander;
              state aaa_aaa;
              state aaa_aab;

            };
            state aaa_ab {
              state aaa_aba;
              initial state aaa_abb;
            };
          };
          state aaa_b {
            state aaa_ba {
              state aaa_baa;
              initial state aaa_bab;
            };
            initial state aaa_bb { initial state aaa_bba; };
          };
        };
        state aab {
          initial state aab_bystander;
          state aab_a {
            state aab_aa {
              initial state aab_aaa;
            };
            initial state aab_ab { initial state aab_aba; };
          };
        };
      };
      state ab {
        initial state ab_bystander;
        state aba {
          initial state aba_bystander;
          state aba_a {
            initial state aba_aa {
              initial state aba_aaa;
            };
         };
        };
      };
    };

    state b {
      initial state b_bystander;
      state ba {
        initial state ba_bystander;
        state baa;
      };
    };
  }
}
