/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical.actions;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test={
  [Sync<String><"N", "N -> a">, Untimed<String><"INIT -> N", "-> N", "~ N", "N ->", "N -> a", "-> a", "~ a">],
  [Sync<String><"N", "N -> b">, Untimed<String><"INIT -> N", "-> N", "~ N", "N ->", "N -> b", "-> b", "-> bb", "~ b", "~ bb">],
  [Sync<String><"N", "N -> c">, Untimed<String><"INIT -> N", "-> N", "~ N", "N ->", "N -> c", "-> c", "-> cc", "-> ccc", "~ c", "~ cc", "~ ccc">],
  [Sync<String><"N", "N -> d">, Untimed<String><"INIT -> N", "-> N", "~ N", "N ->", "N -> d", "-> d", "-> dd", "-> ddd", "-> ddd_d", "~ d", "~ dd", "~ ddd", "~ ddd_d">],
  [Sync<String><"N", "N -> bb">, Untimed<String><"INIT -> N", "-> N", "~ N", "N ->", "N -> bb", "-> b", "-> bb", "~ b", "~ bb">],
  [Sync<String><"N", "N -> ccc">, Untimed<String><"INIT -> N", "-> N", "~ N", "N ->", "N -> ccc", "-> c", "-> cc", "-> ccc", "~ c", "~ cc", "~ ccc">],
  [Sync<String><"N", "N -> ddd">, Untimed<String><"INIT -> N", "-> N", "~ N", "N ->", "N -> ddd", "-> d", "-> dd", "-> ddd", "-> ddd_d", "~ d", "~ dd", "~ ddd", "~ ddd_d">],
  [Sync<String><"N", "N -> eee">, Untimed<String><"INIT -> N", "-> N", "~ N", "N ->", "N -> eee", "-> e", "-> ee", "-> eee", "-> eee_e", "-> eee_ee", "~ e", "~ ee", "~ eee", "~ eee_e", "~ eee_ee">],
  [Sync<String><"N", "N -> fff">, Untimed<String><"INIT -> N", "-> N", "~ N", "N ->", "N -> fff", "-> f", "-> ff", "-> fff", "-> fff_f", "-> fff_ff", "-> fff_fff", "~ f", "~ ff", "~ fff", "~ fff_f", "~ fff_ff", "~ fff_fff">]
}, ticks=[2, 2, 2, 2, 2, 2, 2, 2, 2]>>
component NestedTransitionTargetsTest(SyncStream<String> input, UntimedStream<String> expected) {
  NestedTransitionTargets sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<String> generator(input);

  AssertEqualsUntimed<String> assertions(expected);
}
