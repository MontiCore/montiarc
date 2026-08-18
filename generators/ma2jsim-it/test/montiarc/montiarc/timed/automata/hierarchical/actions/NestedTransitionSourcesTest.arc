/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical.actions;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test={
  [Event<String><"a", Tick, "a -> N", Tick>, Event<String><"INIT -> a", "-> a", "~ a", Tick, "a ->", "a -> N", "-> N", "~ N", Tick>],
  [Event<String><"a", "a -> N", Tick>, Event<String><"INIT -> a", "-> a", "a ->", "a -> N", "-> N", "~ N", Tick>],
  [Event<String><"b", Tick, "b -> N", Tick>, Event<String><"INIT -> b", "-> b", "-> bb", "~ b", "~ bb", Tick, "bb ->", "b ->", "b -> N", "-> N", "~ N", Tick>],
  [Event<String><"b", "b -> N", Tick>, Event<String><"INIT -> b", "-> b", "-> bb", "bb ->", "b ->", "b -> N", "-> N", "~ N", Tick>],
  [Event<String><"c", Tick, "c -> N", Tick>, Event<String><"INIT -> c", "-> c", "-> cc", "-> ccc", "~ c", "~ cc", "~ ccc", Tick, "ccc ->", "cc ->", "c ->", "c -> N", "-> N", "~ N", Tick>],
  [Event<String><"c", "c -> N", Tick>, Event<String><"INIT -> c", "-> c", "-> cc", "-> ccc", "ccc ->", "cc ->", "c ->", "c -> N", "-> N", "~ N", Tick>],
  [Event<String><"d", Tick, "d -> N", Tick>, Event<String><"INIT -> d", "-> d", "-> dd", "-> ddd", "-> ddd_d", "~ d", "~ dd", "~ ddd", "~ ddd_d", Tick, "ddd_d ->", "ddd ->", "dd ->", "d ->", "d -> N", "-> N", "~ N", Tick>],
  [Event<String><"d", "d -> N", Tick>, Event<String><"INIT -> d", "-> d", "-> dd", "-> ddd", "-> ddd_d", "ddd_d ->", "ddd ->", "dd ->", "d ->", "d -> N", "-> N", "~ N", Tick>],
  [Event<String><"b", Tick, "bb -> N", Tick>, Event<String><"INIT -> b", "-> b", "-> bb", "~ b", "~ bb", Tick, "bb ->", "b ->", "bb -> N", "-> N", "~ N", Tick>],
  [Event<String><"b", "bb -> N", Tick>, Event<String><"INIT -> b", "-> b", "-> bb", "bb ->", "b ->", "bb -> N", "-> N", "~ N", Tick>],
  [Event<String><"c", Tick, "ccc -> N", Tick>, Event<String><"INIT -> c", "-> c", "-> cc", "-> ccc", "~ c", "~ cc", "~ ccc", Tick, "ccc ->", "cc ->", "c ->", "ccc -> N", "-> N", "~ N", Tick>],
  [Event<String><"c", "ccc -> N", Tick>, Event<String><"INIT -> c", "-> c", "-> cc", "-> ccc", "ccc ->", "cc ->", "c ->", "ccc -> N", "-> N", "~ N", Tick>],
  [Event<String><"d", Tick, "ddd -> N", Tick>, Event<String><"INIT -> d", "-> d", "-> dd", "-> ddd", "-> ddd_d", "~ d", "~ dd", "~ ddd", "~ ddd_d", Tick, "ddd_d ->", "ddd ->", "dd ->", "d ->", "ddd -> N", "-> N", "~ N", Tick>],
  [Event<String><"d", "ddd -> N", Tick>, Event<String><"INIT -> d", "-> d", "-> dd", "-> ddd", "-> ddd_d", "ddd_d ->", "ddd ->", "dd ->", "d ->", "ddd -> N", "-> N", "~ N", Tick>],
  [Event<String><"e", Tick, "eee -> N", Tick>, Event<String><"INIT -> e", "-> e", "-> ee", "-> eee", "-> eee_e", "-> eee_ee", "~ e", "~ ee", "~ eee", "~ eee_e", "~ eee_ee", Tick, "eee_ee ->", "eee_e ->", "eee ->", "ee ->", "e ->", "eee -> N", "-> N", "~ N", Tick>],
  [Event<String><"e", "eee -> N", Tick>, Event<String><"INIT -> e", "-> e", "-> ee", "-> eee", "-> eee_e", "-> eee_ee", "eee_ee ->", "eee_e ->", "eee ->", "ee ->", "e ->", "eee -> N", "-> N", "~ N", Tick>],
  [Event<String><"f", Tick, "fff -> N", Tick>, Event<String><"INIT -> f", "-> f", "-> ff", "-> fff", "-> fff_f", "-> fff_ff", "-> fff_fff", "~ f", "~ ff", "~ fff", "~ fff_f", "~ fff_ff", "~ fff_fff", Tick, "fff_fff ->", "fff_ff ->", "fff_f ->", "fff ->", "ff ->", "f ->", "fff -> N", "-> N", "~ N", Tick>],
  [Event<String><"f", "fff -> N", Tick>, Event<String><"INIT -> f", "-> f", "-> ff", "-> fff", "-> fff_f", "-> fff_ff", "-> fff_fff", "fff_fff ->", "fff_ff ->", "fff_f ->", "fff ->", "ff ->", "f ->", "fff -> N", "-> N", "~ N", Tick>]
}, ticks=[2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1]>>
component NestedTransitionSourcesTest(EventStream<String> input, EventStream<String> expected) {
  NestedTransitionSources sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<String> generator(input);

  AssertEqualsTimed<String> assertions(expected);
}
