/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test={
  [Sync<String><"a", "a -> N", "check">, Untimed<String><"-> a", "a -> N", "Neutral">],
  [Sync<String><"b", "check", "b -> N", "check">, Untimed<String><"-> b", "bb", "b -> N", "Neutral">],
  [Sync<String><"c", "check", "c -> N", "check">, Untimed<String><"-> c", "ccc", "c -> N", "Neutral">],
  [Sync<String><"d", "check", "d -> N", "check">, Untimed<String><"-> d", "ddd_d", "d -> N", "Neutral">],
  [Sync<String><"b", "check", "bb -> N", "check">, Untimed<String><"-> b", "bb", "bb -> N", "Neutral">],
  [Sync<String><"c", "check", "ccc -> N", "check">, Untimed<String><"-> c", "ccc", "ccc -> N", "Neutral">],
  [Sync<String><"d", "check", "ddd -> N", "check">, Untimed<String><"-> d", "ddd_d", "ddd -> N", "Neutral">],
  [Sync<String><"e", "check", "eee -> N", "check">, Untimed<String><"-> e", "eee_ee", "eee -> N", "Neutral">],
  [Sync<String><"f", "check", "fff -> N", "check">, Untimed<String><"-> f", "fff_fff", "fff -> N", "Neutral">]
}, ticks=[3, 4, 4, 4, 4, 4, 4, 4, 4]>>
component NestedTransitionSourcesTest(SyncStream<String> input, UntimedStream<String> expected) {
  NestedTransitionSources sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<String> generator(input);

  AssertEqualsUntimed<String> assertions(expected);
}
