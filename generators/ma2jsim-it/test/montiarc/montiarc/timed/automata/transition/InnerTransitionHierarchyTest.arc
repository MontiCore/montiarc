/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test={
  [Event<int><Tick>,          Event<String><"Entry_S", "Entry_A", Tick>],
  [Event<int><1, Tick>,       Event<String><"Entry_S", "Entry_A", "Exit_A", "Inner Trans 1", "Entry_A", Tick>],
  [Event<int><1, Tick, 1, Tick>, Event<String><"Entry_S", "Entry_A", "Exit_A", "Inner Trans 1", "Entry_A", Tick, "Exit_A", "Inner Trans 1", "Entry_A", Tick>],
  [Event<int><1, Tick, 1, Tick, 1, Tick>, Event<String><"Entry_S", "Entry_A", "Exit_A", "Inner Trans 1", "Entry_A", Tick, "Exit_A", "Inner Trans 1", "Entry_A", Tick, "Exit_A", "Inner Trans 1", "Entry_A", Tick>],
  [Event<int><1, Tick, Tick>, Event<String><"Entry_S", "Entry_A", "Exit_A", "Inner Trans 1", "Entry_A", Tick, Tick>],
  [Event<int><2, Tick>,       Event<String><"Entry_S", "Entry_A", "Exit_A", "Trans_A_B", "Entry_B", Tick>]
}, ticks=[1, 1, 2, 3, 2, 1]>>
component InnerTransitionHierarchyTest(EventStream<int> input, EventStream<String> expected) {
  InnerTransitionHierarchy sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<int> generator(input);

  AssertEqualsTimed<String> assertions(expected);
}
