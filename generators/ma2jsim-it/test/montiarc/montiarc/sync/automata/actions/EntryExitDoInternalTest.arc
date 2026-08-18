/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.actions;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test={
  [Sync<String><"noop", "internal", "noop">, Untimed<String><"Enter A", "Do A", "internal A", "Do A", "Do A">],
  [Sync<String><"loop", "loop">, Untimed<String><"Enter A", "Exit A", "A -> A", "Enter A", "Do A", "Exit A", "A -> A", "Enter A", "Do A">],
  [Sync<String><"switch", "switch", "noop">, Untimed<String><"Enter A", "Exit A", "A -> B", "Enter B", "Do B", "Exit B", "B -> A", "Enter A", "Do A", "Do A">],
  [Sync<String><"switch", "loop", "loop">, Untimed<String><"Enter A", "Exit A", "A -> B", "Enter B", "Do B", "Exit B", "B -> B", "Enter B", "Do B", "Exit B", "B -> B", "Enter B", "Do B">],
  [Sync<String><"switch", "noop", "loop", "switch", "noop">, Untimed<String><"Enter A", "Exit A", "A -> B", "Enter B", "Do B", "Do B", "Exit B", "B -> B", "Enter B", "Do B", "Exit B", "B -> A", "Enter A", "Do A", "Do A">]
}, ticks=[3, 2, 3, 3, 5]>>
component EntryExitDoInternalTest(SyncStream<String> input, UntimedStream<String> expected) {
  EntryExitDoInternal sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<String> generator(input);

  AssertEqualsUntimed<String> assertions(expected);
}
