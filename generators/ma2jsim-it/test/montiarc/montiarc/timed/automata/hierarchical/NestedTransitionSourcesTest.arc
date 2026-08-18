/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test={
  [Event<String><"a", "a -> N", "check">, Event<String><"-> a", "a -> N", "Neutral">],
  [Event<String><"b", "check", "b -> N", "check">, Event<String><"-> b", "bb", "b -> N", "Neutral">],
  [Event<String><"c", "check", "c -> N", "check">, Event<String><"-> c", "ccc", "c -> N", "Neutral">],
  [Event<String><"d", "check", "d -> N", "check">, Event<String><"-> d", "ddd_d", "d -> N", "Neutral">],
  [Event<String><"b", "check", "bb -> N", "check">, Event<String><"-> b", "bb", "bb -> N", "Neutral">],
  [Event<String><"c", "check", "ccc -> N", "check">, Event<String><"-> c", "ccc", "ccc -> N", "Neutral">],
  [Event<String><"d", "check", "ddd -> N", "check">, Event<String><"-> d", "ddd_d", "ddd -> N", "Neutral">],
  [Event<String><"e", "check", "eee -> N", "check">, Event<String><"-> e", "eee_ee", "eee -> N", "Neutral">],
  [Event<String><"f", "check", "fff -> N", "check">, Event<String><"-> f", "fff_fff", "fff -> N", "Neutral">]
}, ticks=0>>
component NestedTransitionSourcesTest(EventStream<String> input, EventStream<String> expected) {
  NestedTransitionSources sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<String> generator(input);

  AssertEqualsTimed<String> assertions(expected);
}
