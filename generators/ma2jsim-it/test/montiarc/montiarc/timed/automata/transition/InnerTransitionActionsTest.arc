/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=[1,1,2,3,2], input=[
  <int><Tick>,
  <1, Tick>,
  <1, Tick, 1, Tick>,
  <1, Tick, 1, Tick, 1, Tick>,
  <1, Tick, Tick>
], expected=[
  <String><"Entry_S", Tick>,
  <String><"Entry_S", "Inner Trans", Tick>,
  <String><"Entry_S", "Inner Trans", Tick, "Inner Trans", Tick>,
  <String><"Entry_S", "Inner Trans", Tick, "Inner Trans", Tick, "Inner Trans", Tick>,
  <String><"Entry_S", "Inner Trans", Tick, Tick>
]>>
component InnerTransitionActionsTest(EventStream<int> input, EventStream<String> expected) {
  montiarc.timed.automata.transition.InnerTransitionActions sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<Integer> generator(input);

  AssertEqualsTimed<String> assertions(expected);
}
