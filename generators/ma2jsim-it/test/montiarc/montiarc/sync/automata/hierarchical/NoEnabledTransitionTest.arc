/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test={
  [Sync<String><"aa", "check", "This message should not trigger any transition", "check">, Untimed<String><"-> aa", "aa", "aa">],
  [Sync<String><"bbb", "check", "This message should not trigger any transition", "check">, Untimed<String><"-> bbb", "bbb", "bbb">],
  [Sync<String><"ccc_c", "check", "This message should not trigger any transition", "check">, Untimed<String><"-> ccc_c", "ccc_c", "ccc_c">],
  [Sync<String><"ddd_dd", "check", "This message should not trigger any transition", "check">, Untimed<String><"-> ddd_dd", "ddd_dd", "ddd_dd">]
}, ticks=[4, 4, 4, 4]>>
component NoEnabledTransitionTest(SyncStream<String> input, UntimedStream<String> expected) {
  NoEnabledTransition sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<String> generator(input);

  AssertEqualsUntimed<String> assertions(expected);
}
