/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.transition;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test={
  [Sync<int><>, Untimed<String><"Entry_S", "Entry_A", "Exit_A", "Inner Trans 2", "Entry_A">],
  [Sync<1>,     Untimed<String><"Entry_S", "Entry_A", "Exit_A", "Inner Trans 1", "Entry_A">],
  [Sync<1, 1>,  Untimed<String><"Entry_S", "Entry_A", "Exit_A", "Inner Trans 1", "Entry_A", "Exit_A", "Inner Trans 1", "Entry_A">],
  [Sync<1, 1, 1>, Untimed<String><"Entry_S", "Entry_A", "Exit_A", "Inner Trans 1", "Entry_A", "Exit_A", "Inner Trans 1", "Entry_A", "Exit_A", "Inner Trans 1", "Entry_A">],
  [Sync<1, 0>,  Untimed<String><"Entry_S", "Entry_A", "Exit_A", "Inner Trans 1", "Entry_A", "Exit_A", "Inner Trans 2", "Entry_A">],
  [Sync<2>,     Untimed<String><"Entry_S", "Entry_A", "Exit_A", "Trans_A_B", "Entry_B">]
}, ticks=[1, 1, 2, 3, 2, 1]>>
component InnerTransitionHierarchyTest(SyncStream<int> input, UntimedStream<String> expected) {
  InnerTransitionHierarchy sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<int> generator(input);

  AssertEqualsUntimed<String> assertions(expected);
}
