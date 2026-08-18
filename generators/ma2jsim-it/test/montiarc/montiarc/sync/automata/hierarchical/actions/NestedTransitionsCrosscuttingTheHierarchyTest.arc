/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical.actions;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test={
  [Sync<String><"aaa_aaa", "aaa_aaa -> aaa_aab">, Untimed<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", "aaa_aaa ->", "aaa_aaa -> aaa_aab", "-> aaa_aab", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aab">],
  [Sync<String><"aaa_aaa", "aaa_aaa -> aaa_aba">, Untimed<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", "aaa_aaa ->", "aaa_aa ->", "aaa_aaa -> aaa_aba", "-> aaa_ab", "-> aaa_aba", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_ab", "~ aaa_aba">],
  [Sync<String><"aaa_aaa", "aaa_aaa -> aaa_baa">, Untimed<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa_aaa -> aaa_baa", "-> aaa_b", "-> aaa_ba", "-> aaa_baa", "~ a", "~ aa", "~ aaa", "~ aaa_b", "~ aaa_ba", "~ aaa_baa">],
  [Sync<String><"aaa_aaa", "aaa_aa -> aaa_ab">, Untimed<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", "aaa_aaa ->", "aaa_aa ->", "aaa_aa -> aaa_ab", "-> aaa_ab", "-> aaa_abb", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_ab", "~ aaa_abb">],
  [Sync<String><"aaa_aaa", "aaa_aa -> aaa_ba">, Untimed<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa_aa -> aaa_ba", "-> aaa_b", "-> aaa_ba", "-> aaa_bab", "~ a", "~ aa", "~ aaa", "~ aaa_b", "~ aaa_ba", "~ aaa_bab">],
  [Sync<String><"aaa_aaa", "aaa_aa -> aab_aa">, Untimed<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa ->", "aaa_aa -> aab_aa", "-> aab", "-> aab_a", "-> aab_aa", "-> aab_aaa", "~ a", "~ aa", "~ aab", "~ aab_a", "~ aab_aa", "~ aab_aaa">],
  [Sync<String><"aaa_aaa", "aaa_a -> aaa_b">, Untimed<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa_a -> aaa_b", "-> aaa_b", "-> aaa_bb", "-> aaa_bba", "~ a", "~ aa", "~ aaa", "~ aaa_b", "~ aaa_bb", "~ aaa_bba">],
  [Sync<String><"aaa_aaa", "aaa_a -> aab_a">, Untimed<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa ->", "aaa_a -> aab_a", "-> aab", "-> aab_a", "-> aab_ab", "-> aab_aba", "~ a", "~ aa", "~ aab", "~ aab_a", "~ aab_ab", "~ aab_aba">],
  [Sync<String><"aaa_aaa", "aaa_a -> aba_a">, Untimed<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa ->", "aa ->", "aaa_a -> aba_a", "-> ab", "-> aba", "-> aba_a", "-> aba_aa", "-> aba_aaa", "~ a", "~ ab", "~ aba", "~ aba_a", "~ aba_aa", "~ aba_aaa">],
  [Sync<String><"aaa_aaa", "aa -> baa">, Untimed<String><"INIT -> aaa_aaa", "-> a", "-> aa", "-> aaa", "-> aaa_a", "-> aaa_aa", "-> aaa_aaa", "~ a", "~ aa", "~ aaa", "~ aaa_a", "~ aaa_aa", "~ aaa_aaa", "aaa_aaa ->", "aaa_aa ->", "aaa_a ->", "aaa ->", "aa ->", "a ->", "aa -> baa", "-> b", "-> ba", "-> baa", "~ b", "~ ba", "~ baa">]
}, ticks=[2, 2, 2, 2, 2, 2, 2, 2, 2, 2]>>
component NestedTransitionsCrosscuttingTheHierarchyTest(SyncStream<String> input, UntimedStream<String> expected) {
  NestedTransitionsCrosscuttingTheHierarchy sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<String> generator(input);

  AssertEqualsUntimed<String> assertions(expected);
}
