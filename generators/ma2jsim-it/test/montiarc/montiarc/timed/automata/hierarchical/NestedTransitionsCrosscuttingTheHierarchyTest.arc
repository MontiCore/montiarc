/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test={
  [Event<String><"aaa_aaa", Tick, "check", Tick, "aaa_aaa -> aaa_aab", Tick, "check", Tick>, Event<String><"-> aaa_aaa", Tick, "aaa_aaa", Tick, "aaa_aaa -> aaa_aab", Tick, "aaa_aab", Tick>],
  [Event<String><"aaa_aaa", Tick, "check", Tick, "aaa_aaa -> aaa_aba", Tick, "check", Tick>, Event<String><"-> aaa_aaa", Tick, "aaa_aaa", Tick, "aaa_aaa -> aaa_aba", Tick, "aaa_aba", Tick>],
  [Event<String><"aaa_aaa", Tick, "check", Tick, "aaa_aaa -> aaa_baa", Tick, "check", Tick>, Event<String><"-> aaa_aaa", Tick, "aaa_aaa", Tick, "aaa_aaa -> aaa_baa", Tick, "aaa_baa", Tick>],
  [Event<String><"aaa_aaa", Tick, "check", Tick, "aaa_aa -> aaa_ab", Tick, "check", Tick>, Event<String><"-> aaa_aaa", Tick, "aaa_aaa", Tick, "aaa_aa -> aaa_ab", Tick, "aaa_abb", Tick>],
  [Event<String><"aaa_aaa", Tick, "check", Tick, "aaa_aa -> aaa_ba", Tick, "check", Tick>, Event<String><"-> aaa_aaa", Tick, "aaa_aaa", Tick, "aaa_aa -> aaa_ba", Tick, "aaa_bab", Tick>],
  [Event<String><"aaa_aaa", Tick, "check", Tick, "aaa_aa -> aab_aa", Tick, "check", Tick>, Event<String><"-> aaa_aaa", Tick, "aaa_aaa", Tick, "aaa_aa -> aab_aa", Tick, "aab_aaa", Tick>],
  [Event<String><"aaa_aaa", Tick, "check", Tick, "aaa_a -> aaa_b", Tick, "check", Tick>, Event<String><"-> aaa_aaa", Tick, "aaa_aaa", Tick, "aaa_a -> aaa_b", Tick, "aaa_bba", Tick>],
  [Event<String><"aaa_aaa", Tick, "check", Tick, "aaa_a -> aab_a", Tick, "check", Tick>, Event<String><"-> aaa_aaa", Tick, "aaa_aaa", Tick, "aaa_a -> aab_a", Tick, "aab_aba", Tick>],
  [Event<String><"aaa_aaa", Tick, "check", Tick, "aaa_a -> aba_a", Tick, "check", Tick>, Event<String><"-> aaa_aaa", Tick, "aaa_aaa", Tick, "aaa_a -> aba_a", Tick, "aba_aaa", Tick>],
  [Event<String><"aaa_aaa", Tick, "check", Tick, "aa -> baa", Tick, "check", Tick>, Event<String><"-> aaa_aaa", Tick, "aaa_aaa", Tick, "aa -> baa", Tick, "baa", Tick>]
}, ticks=[4, 4, 4, 4, 4, 4, 4, 4, 4, 4]>>
component NestedTransitionsCrosscuttingTheHierarchyTest(EventStream<String> input, EventStream<String> expected) {
  NestedTransitionsCrosscuttingTheHierarchy sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<String> generator(input);

  AssertEqualsTimed<String> assertions(expected);
}
