/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical.actions;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test={
  [Event<String><"N", Tick, "N -> a", Tick>, Event<String><"INIT -> N", "-> N", "~ N", Tick, "N ->", "N -> a", "-> a", "~ a", Tick>],
  [Event<String><"N", "N -> a", Tick>, Event<String><"INIT -> N", "-> N", "N ->", "N -> a", "-> a", "~ a", Tick>],
  [Event<String><"N", Tick, "N -> b", Tick>, Event<String><"INIT -> N", "-> N", "~ N", Tick, "N ->", "N -> b", "-> b", "-> bb", "~ b", "~ bb", Tick>],
  [Event<String><"N", "N -> b", Tick>, Event<String><"INIT -> N", "-> N", "N ->", "N -> b", "-> b", "-> bb", "~ b", "~ bb", Tick>],
  [Event<String><"N", Tick, "N -> c", Tick>, Event<String><"INIT -> N", "-> N", "~ N", Tick, "N ->", "N -> c", "-> c", "-> cc", "-> ccc", "~ c", "~ cc", "~ ccc", Tick>],
  [Event<String><"N", "N -> c", Tick>, Event<String><"INIT -> N", "-> N", "N ->", "N -> c", "-> c", "-> cc", "-> ccc", "~ c", "~ cc", "~ ccc", Tick>],
  [Event<String><"N", Tick, "N -> d", Tick>, Event<String><"INIT -> N", "-> N", "~ N", Tick, "N ->", "N -> d", "-> d", "-> dd", "-> ddd", "-> ddd_d", "~ d", "~ dd", "~ ddd", "~ ddd_d", Tick>],
  [Event<String><"N", "N -> d", Tick>, Event<String><"INIT -> N", "-> N", "N ->", "N -> d", "-> d", "-> dd", "-> ddd", "-> ddd_d", "~ d", "~ dd", "~ ddd", "~ ddd_d", Tick>],
  [Event<String><"N", Tick, "N -> bb", Tick>, Event<String><"INIT -> N", "-> N", "~ N", Tick, "N ->", "N -> bb", "-> b", "-> bb", "~ b", "~ bb", Tick>],
  [Event<String><"N", "N -> bb", Tick>, Event<String><"INIT -> N", "-> N", "N ->", "N -> bb", "-> b", "-> bb", "~ b", "~ bb", Tick>],
  [Event<String><"N", Tick, "N -> ccc", Tick>, Event<String><"INIT -> N", "-> N", "~ N", Tick, "N ->", "N -> ccc", "-> c", "-> cc", "-> ccc", "~ c", "~ cc", "~ ccc", Tick>],
  [Event<String><"N", "N -> ccc", Tick>, Event<String><"INIT -> N", "-> N", "N ->", "N -> ccc", "-> c", "-> cc", "-> ccc", "~ c", "~ cc", "~ ccc", Tick>],
  [Event<String><"N", Tick, "N -> ddd", Tick>, Event<String><"INIT -> N", "-> N", "~ N", Tick, "N ->", "N -> ddd", "-> d", "-> dd", "-> ddd", "-> ddd_d", "~ d", "~ dd", "~ ddd", "~ ddd_d", Tick>],
  [Event<String><"N", "N -> ddd", Tick>, Event<String><"INIT -> N", "-> N", "N ->", "N -> ddd", "-> d", "-> dd", "-> ddd", "-> ddd_d", "~ d", "~ dd", "~ ddd", "~ ddd_d", Tick>],
  [Event<String><"N", Tick, "N -> eee", Tick>, Event<String><"INIT -> N", "-> N", "~ N", Tick, "N ->", "N -> eee", "-> e", "-> ee", "-> eee", "-> eee_e", "-> eee_ee", "~ e", "~ ee", "~ eee", "~ eee_e", "~ eee_ee", Tick>],
  [Event<String><"N", "N -> eee", Tick>, Event<String><"INIT -> N", "-> N", "N ->", "N -> eee", "-> e", "-> ee", "-> eee", "-> eee_e", "-> eee_ee", "~ e", "~ ee", "~ eee", "~ eee_e", "~ eee_ee", Tick>],
  [Event<String><"N", Tick, "N -> fff", Tick>, Event<String><"INIT -> N", "-> N", "~ N", Tick, "N ->", "N -> fff", "-> f", "-> ff", "-> fff", "-> fff_f", "-> fff_ff", "-> fff_fff", "~ f", "~ ff", "~ fff", "~ fff_f", "~ fff_ff", "~ fff_fff", Tick>],
  [Event<String><"N", "N -> fff", Tick>, Event<String><"INIT -> N", "-> N", "N ->", "N -> fff", "-> f", "-> ff", "-> fff", "-> fff_f", "-> fff_ff", "-> fff_fff", "~ f", "~ ff", "~ fff", "~ fff_f", "~ fff_ff", "~ fff_fff", Tick>]
}, ticks=[2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1]>>
component NestedTransitionTargetsTest(EventStream<String> input, EventStream<String> expected) {
  NestedTransitionTargets sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<String> generator(input);

  AssertEqualsTimed<String> assertions(expected);
}
