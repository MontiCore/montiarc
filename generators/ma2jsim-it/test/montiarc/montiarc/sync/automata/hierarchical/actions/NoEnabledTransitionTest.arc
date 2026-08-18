/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.hierarchical.actions;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test={
  [Sync<String><"aa", "no-trigger", "no-trigger">, Untimed<String><"INIT -> aa", "-> a", "-> aa", "~ a", "~ aa", "~ a", "~ aa", "~ a", "~ aa">],
  [Sync<String><"bbb", "no-trigger", "no-trigger">, Untimed<String><"INIT -> bbb", "-> b", "-> bb", "-> bbb", "~ b", "~ bb", "~ bbb", "~ b", "~ bb", "~ bbb", "~ b", "~ bb", "~ bbb">],
  [Sync<String><"ccc_c", "no-trigger", "no-trigger">, Untimed<String><"INIT -> ccc_c", "-> c", "-> cc", "-> ccc", "-> ccc_c", "~ c", "~ cc", "~ ccc", "~ ccc_c", "~ c", "~ cc", "~ ccc", "~ ccc_c", "~ c", "~ cc", "~ ccc", "~ ccc_c">],
  [Sync<String><"ddd_dd", "no-trigger", "no-trigger">, Untimed<String><"INIT -> ddd_dd", "-> d", "-> dd", "-> ddd", "-> ddd_d", "-> ddd_dd", "~ d", "~ dd", "~ ddd", "~ ddd_d", "~ ddd_dd", "~ d", "~ dd", "~ ddd", "~ ddd_d", "~ ddd_dd", "~ d", "~ dd", "~ ddd", "~ ddd_d", "~ ddd_dd">]
}, ticks=[3, 3, 3, 3]>>
component NoEnabledTransitionTest(SyncStream<String> input, UntimedStream<String> expected) {
  NoEnabledTransition sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<String> generator(input);

  AssertEqualsUntimed<String> assertions(expected);
}
