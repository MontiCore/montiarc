/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical.actions;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test={
  [Event<String><"aaa_aaa", Tick, "aaa_aaa -> aaa_aab", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", Tick, "aaa_aaa ->", "aaa_aaa -> aaa_aab", "-> aaa_aab", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aab", Tick>],
  [Event<String><"aaa_aaa", Tick, "aaa_aaa -> aaa_aba", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", Tick, "aaa_aaa ->", "aaa_aa ->", "aaa_aaa -> aaa_aba", "-> aaa_ab", "-> aaa_aba", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_ab", "~ aaa_aba", Tick>],
  [Event<String><"aaa_aaa", Tick, "aaa_aaa -> aaa_baa", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", Tick, "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa_aaa -> aaa_baa", "-> aaa_b", "-> aaa_ba", "-> aaa_baa", "~ a", "~ aa", "~ aaa", "~ aaa_b", "~ aaa_ba", "~ aaa_baa", Tick>],
  [Event<String><"aaa_aaa", Tick, "aaa_aa -> aaa_ab", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", Tick, "aaa_aaa ->", "aaa_aa ->", "aaa_aa -> aaa_ab", "-> aaa_ab", "-> aaa_abb", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_ab", "~ aaa_abb", Tick>],
  [Event<String><"aaa_aaa", Tick, "aaa_aa -> aaa_ba", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", Tick, "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa_aa -> aaa_ba", "-> aaa_b", "-> aaa_ba", "-> aaa_bab", "~ a", "~ aa", "~ aaa", "~ aaa_b", "~ aaa_ba", "~ aaa_bab", Tick>],
  [Event<String><"aaa_aaa", Tick, "aaa_aa -> aab_aa", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", Tick, "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa ->", "aaa_aa -> aab_aa", "-> aab", "-> aab_a", "-> aab_aa", "-> aab_aaa", "~ a", "~ aa", "~ aab", "~ aab_a", "~ aab_aa", "~ aab_aaa", Tick>],
  [Event<String><"aaa_aaa", Tick, "aaa_a -> aaa_b", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", Tick, "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa_a -> aaa_b", "-> aaa_b", "-> aaa_bb", "-> aaa_bba", "~ a", "~ aa", "~ aaa", "~ aaa_b", "~ aaa_bb", "~ aaa_bba", Tick>],
  [Event<String><"aaa_aaa", Tick, "aaa_a -> aab_a", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", Tick, "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa ->", "aaa_a -> aab_a", "-> aab", "-> aab_a", "-> aab_ab", "-> aab_aba", "~ a", "~ aa", "~ aab", "~ aab_a", "~ aab_ab", "~ aab_aba", Tick>],
  [Event<String><"aaa_aaa", Tick, "aaa_a -> aba_a", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", Tick, "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa ->", "aa ->", "aaa_a -> aba_a", "-> ab", "-> aba", "-> aba_a", "-> aba_aa", "-> aba_aaa", "~ a", "~ ab", "~ aba", "~ aba_a", "~ aba_aa", "~ aba_aaa", Tick>],
  [Event<String><"aaa_aaa", Tick, "aa -> baa", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", Tick, "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa ->", "aa ->", "a ->", "aa -> baa", "-> b", "-> ba", "-> baa", "~ b", "~ ba", "~ baa", Tick>],
  [Event<String><"aaa_aaa", "aaa_aaa -> aaa_aab", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "aaa_aaa ->", "aaa_aaa -> aaa_aab", "-> aaa_aab", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aab", Tick>],
  [Event<String><"aaa_aaa", "aaa_aaa -> aaa_aba", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "aaa_aaa ->", "aaa_aa ->", "aaa_aaa -> aaa_aba", "-> aaa_ab", "-> aaa_aba", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_ab", "~ aaa_aba", Tick>],
  [Event<String><"aaa_aaa", "aaa_aaa -> aaa_baa", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa_aaa -> aaa_baa", "-> aaa_b", "-> aaa_ba", "-> aaa_baa", "~ a", "~ aa", "~ aaa", "~ aaa_b", "~ aaa_ba", "~ aaa_baa", Tick>],
  [Event<String><"aaa_aaa", "aaa_aa -> aaa_ab", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "aaa_aaa ->", "aaa_aa ->", "aaa_aa -> aaa_ab", "-> aaa_ab", "-> aaa_abb", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_ab", "~ aaa_abb", Tick>],
  [Event<String><"aaa_aaa", "aaa_aa -> aaa_ba", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa_aa -> aaa_ba", "-> aaa_b", "-> aaa_ba", "-> aaa_bab", "~ a", "~ aa", "~ aaa", "~ aaa_b", "~ aaa_ba", "~ aaa_bab", Tick>],
  [Event<String><"aaa_aaa", "aaa_aa -> aab_aa", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa ->", "aaa_aa -> aab_aa", "-> aab", "-> aab_a", "-> aab_aa", "-> aab_aaa", "~ a", "~ aa", "~ aab", "~ aab_a", "~ aab_aa", "~ aab_aaa", Tick>],
  [Event<String><"aaa_aaa", "aaa_a -> aaa_b", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa_a -> aaa_b", "-> aaa_b", "-> aaa_bb", "-> aaa_bba", "~ a", "~ aa", "~ aaa", "~ aaa_b", "~ aaa_bb", "~ aaa_bba", Tick>],
  [Event<String><"aaa_aaa", "aaa_a -> aab_a", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa ->", "aaa_a -> aab_a", "-> aab", "-> aab_a", "-> aab_ab", "-> aab_aba", "~ a", "~ aa", "~ aab", "~ aab_a", "~ aab_ab", "~ aab_aba", Tick>],
  [Event<String><"aaa_aaa", "aaa_a -> aba_a", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa ->", "aa ->", "aaa_a -> aba_a", "-> ab", "-> aba", "-> aba_a", "-> aba_aa", "-> aba_aaa", "~ a", "~ ab", "~ aba", "~ aba_a", "~ aba_aa", "~ aba_aaa", Tick>],
  [Event<String><"aaa_aaa", "aa -> baa", Tick>, Event<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa ->", "aa ->", "a ->", "aa -> baa", "-> b", "-> ba", "-> baa", "~ b", "~ ba", "~ baa", Tick>]
}, ticks=[2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]>>
component NestedTransitionsCrosscuttingTheHierarchyTest(EventStream<String> input, EventStream<String> expected) {
  NestedTransitionsCrosscuttingTheHierarchy sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<String> generator(input);

  AssertEqualsTimed<String> assertions(expected);
}
