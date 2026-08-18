/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test={
  [Sync<String><"Neutral", "N -> a", "check">, Untimed<String><"-> Neutral", "N -> a", "a">],
  [Sync<String><"Neutral", "N -> b", "check">, Untimed<String><"-> Neutral", "N -> b", "bb">],
  [Sync<String><"Neutral", "N -> c", "check">, Untimed<String><"-> Neutral", "N -> c", "ccc">],
  [Sync<String><"Neutral", "N -> d", "check">, Untimed<String><"-> Neutral", "N -> d", "ddd_d">],
  [Sync<String><"Neutral", "N -> bb", "check">, Untimed<String><"-> Neutral", "N -> bb", "bb">],
  [Sync<String><"Neutral", "N -> ccc", "check">, Untimed<String><"-> Neutral", "N -> ccc", "ccc">],
  [Sync<String><"Neutral", "N -> ddd", "check">, Untimed<String><"-> Neutral", "N -> ddd", "ddd_d">],
  [Sync<String><"Neutral", "N -> eee", "check">, Untimed<String><"-> Neutral", "N -> eee", "eee_ee">],
  [Sync<String><"Neutral", "N -> fff", "check">, Untimed<String><"-> Neutral", "N -> fff", "fff_fff">]
}, ticks=[3, 3, 3, 3, 3, 3, 3, 3, 3]>>
component NestedTransitionTargetsTest(SyncStream<String> input, UntimedStream<String> expected) {
  NestedTransitionTargets sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<String> generator(input);

  AssertEqualsUntimed<String> assertions(expected);
}
