/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical.actions;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test={
  [Sync<String><"a", "a -> N">, Untimed<String><"INIT -> a", "-> a", "~ a", "a ->", "a -> N", "-> N", "~ N">],
  [Sync<String><"b", "b -> N">, Untimed<String><"INIT -> b", "-> b", "-> bb", "~ b", "~ bb", "bb ->", "b ->", "b -> N", "-> N", "~ N">],
  [Sync<String><"c", "c -> N">, Untimed<String><"INIT -> c", "-> c", "-> cc", "-> ccc", "~ c", "~ cc", "~ ccc", "ccc ->", "cc ->", "c ->", "c -> N", "-> N", "~ N">],
  [Sync<String><"d", "d -> N">, Untimed<String><"INIT -> d", "-> d", "-> dd", "-> ddd", "-> ddd_d", "~ d", "~ dd", "~ ddd", "~ ddd_d", "ddd_d ->", "ddd ->", "dd ->", "d ->", "d -> N", "-> N", "~ N">],
  [Sync<String><"b", "bb -> N">, Untimed<String><"INIT -> b", "-> b", "-> bb", "~ b", "~ bb", "bb ->", "b ->", "bb -> N", "-> N", "~ N">],
  [Sync<String><"c", "ccc -> N">, Untimed<String><"INIT -> c", "-> c", "-> cc", "-> ccc", "~ c", "~ cc", "~ ccc", "ccc ->", "cc ->", "c ->", "ccc -> N", "-> N", "~ N">],
  [Sync<String><"d", "ddd -> N">, Untimed<String><"INIT -> d", "-> d", "-> dd", "-> ddd", "-> ddd_d", "~ d", "~ dd", "~ ddd", "~ ddd_d", "ddd_d ->", "ddd ->", "dd ->", "d ->", "ddd -> N", "-> N", "~ N">],
  [Sync<String><"e", "eee -> N">, Untimed<String><"INIT -> e", "-> e", "-> ee", "-> eee", "-> eee_e", "-> eee_ee", "~ e", "~ ee", "~ eee", "~ eee_e", "~ eee_ee", "eee_ee ->", "eee_e ->", "eee ->", "ee ->", "e ->", "eee -> N", "-> N", "~ N">],
  [Sync<String><"f", "fff -> N">, Untimed<String><"INIT -> f", "-> f", "-> ff", "-> fff", "-> fff_f", "-> fff_ff", "-> fff_fff", "~ f", "~ ff", "~ fff", "~ fff_f", "~ fff_ff", "~ fff_fff", "fff_fff ->", "fff_ff ->", "fff_f ->", "fff ->", "ff ->", "f ->", "fff -> N", "-> N", "~ N">]
}, ticks=[2, 2, 2, 2, 2, 2, 2, 2, 2]>>
component NestedTransitionSourcesTest(SyncStream<String> input, UntimedStream<String> expected) {
  NestedTransitionSources sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<String> generator(input);

  AssertEqualsUntimed<String> assertions(expected);
}
