/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.transition;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;

<<test, ticks=[1,2,3], input=[
  Sync<int><>,
  Sync<int><>,
  Sync<int><>
], expected=[
  <String><"Entry_S", "Inner Trans", Tick>,
  <String><"Entry_S", "Inner Trans", Tick, "Inner Trans", Tick>,
  <String><"Entry_S", "Inner Trans", Tick, "Inner Trans", Tick, "Inner Trans", Tick>
]>>
component InnerTransitionActionsTest(SyncStream<int> input, EventStream<String> expected) {
  InnerTransitionActions sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<Integer> generator(input);

  AssertEqualsTimed<String> assertions(expected);
}
