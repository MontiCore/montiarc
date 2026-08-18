/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test={
  [Sync<String><"aaa_aaa", "check", "aaa_aaa -> aaa_aab", "check">, Untimed<String><"-> aaa_aaa", "aaa_aaa", "aaa_aaa -> aaa_aab", "aaa_aab">],
  [Sync<String><"aaa_aaa", "check", "aaa_aaa -> aaa_aba", "check">, Untimed<String><"-> aaa_aaa", "aaa_aaa", "aaa_aaa -> aaa_aba", "aaa_aba">],
  [Sync<String><"aaa_aaa", "check", "aaa_aaa -> aaa_baa", "check">, Untimed<String><"-> aaa_aaa", "aaa_aaa", "aaa_aaa -> aaa_baa", "aaa_baa">],
  [Sync<String><"aaa_aaa", "check", "aaa_aa -> aaa_ab", "check">, Untimed<String><"-> aaa_aaa", "aaa_aaa", "aaa_aa -> aaa_ab", "aaa_abb">],
  [Sync<String><"aaa_aaa", "check", "aaa_aa -> aaa_ba", "check">, Untimed<String><"-> aaa_aaa", "aaa_aaa", "aaa_aa -> aaa_ba", "aaa_bab">],
  [Sync<String><"aaa_aaa", "check", "aaa_aa -> aab_aa", "check">, Untimed<String><"-> aaa_aaa", "aaa_aaa", "aaa_aa -> aab_aa", "aab_aaa">],
  [Sync<String><"aaa_aaa", "check", "aaa_a -> aaa_b", "check">, Untimed<String><"-> aaa_aaa", "aaa_aaa", "aaa_a -> aaa_b", "aaa_bba">],
  [Sync<String><"aaa_aaa", "check", "aaa_a -> aab_a", "check">, Untimed<String><"-> aaa_aaa", "aaa_aaa", "aaa_a -> aab_a", "aab_aba">],
  [Sync<String><"aaa_aaa", "check", "aaa_a -> aba_a", "check">, Untimed<String><"-> aaa_aaa", "aaa_aaa", "aaa_a -> aba_a", "aba_aaa">],
  [Sync<String><"aaa_aaa", "check", "aa -> baa", "check">, Untimed<String><"-> aaa_aaa", "aaa_aaa", "aa -> baa", "baa">]
}, ticks=[4, 4, 4, 4, 4, 4, 4, 4, 4, 4]>>
component NestedTransitionsCrosscuttingTheHierarchyTest(SyncStream<String> input, UntimedStream<String> expected) {
  NestedTransitionsCrosscuttingTheHierarchy sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<String> generator(input);

  AssertEqualsUntimed<String> assertions(expected);
}
