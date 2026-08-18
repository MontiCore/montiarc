/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.actions;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test={
  [Event<String><"noop", Tick, "internal", Tick, Tick>, Event<String><"Enter A", "Do A", Tick, "internal A", "Do A", Tick, "Do A", Tick>],
  [Event<String><"loop", "loop", Tick>, Event<String><"Enter A", "Exit A", "A -> A", "Enter A", "Exit A", "A -> A", "Enter A", "Do A", Tick>],
  [Event<String><"switch", Tick, "switch", Tick, Tick>, Event<String><"Enter A", "Exit A", "A -> B", "Enter B", "Do B", Tick, "Exit B", "B -> A", "Enter A", "Do A", Tick, "Do A", Tick>],
  [Event<String><"switch", Tick, "loop", Tick, "loop", Tick>, Event<String><"Enter A", "Exit A", "A -> B", "Enter B", "Do B", Tick, "Exit B", "B -> B", "Enter B", "Do B", Tick, "Exit B", "B -> B", "Enter B", "Do B", Tick>],
  [Event<String><"switch", Tick, "noop", Tick, "loop", Tick, "switch", Tick, Tick>, Event<String><"Enter A", "Exit A", "A -> B", "Enter B", "Do B", Tick, "Do B", Tick, "Exit B", "B -> B", "Enter B", "Do B", Tick, "Exit B", "B -> A", "Enter A", "Do A", Tick, "Do A", Tick>]
}, ticks=[3, 1, 3, 3, 5]>>
component EntryExitDoInternalTest(EventStream<String> input, EventStream<String> expected) {
  EntryExitDoInternal sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<String> generator(input);

  AssertEqualsTimed<String> assertions(expected);
}
