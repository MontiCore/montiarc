/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test={
  [Event<String><"Neutral", "N -> a", "check">, Event<String><"-> Neutral", "N -> a", "a">],
  [Event<String><"Neutral", "N -> b", "check">, Event<String><"-> Neutral", "N -> b", "bb">],
  [Event<String><"Neutral", "N -> c", "check">, Event<String><"-> Neutral", "N -> c", "ccc">],
  [Event<String><"Neutral", "N -> d", "check">, Event<String><"-> Neutral", "N -> d", "ddd_d">],
  [Event<String><"Neutral", "N -> bb", "check">, Event<String><"-> Neutral", "N -> bb", "bb">],
  [Event<String><"Neutral", "N -> ccc", "check">, Event<String><"-> Neutral", "N -> ccc", "ccc">],
  [Event<String><"Neutral", "N -> ddd", "check">, Event<String><"-> Neutral", "N -> ddd", "ddd_d">],
  [Event<String><"Neutral", "N -> eee", "check">, Event<String><"-> Neutral", "N -> eee", "eee_ee">],
  [Event<String><"Neutral", "N -> fff", "check">, Event<String><"-> Neutral", "N -> fff", "fff_fff">]
}, ticks=0>>
component NestedTransitionTargetsTest(EventStream<String> input, EventStream<String> expected) {
  NestedTransitionTargets sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<String> generator(input);

  AssertEqualsTimed<String> assertions(expected);
}
