/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test={
  [Event<String><"aa", Tick, "check", Tick, "This message should not trigger any transition", Tick, "check", Tick>, Event<String><"-> aa", Tick, "aa", Tick, Tick, "aa", Tick>],
  [Event<String><"bbb", Tick, "check", Tick, "This message should not trigger any transition", Tick, "check", Tick>, Event<String><"-> bbb", Tick, "bbb", Tick, Tick, "bbb", Tick>],
  [Event<String><"ccc_c", Tick, "check", Tick, "This message should not trigger any transition", Tick, "check", Tick>, Event<String><"-> ccc_c", Tick, "ccc_c", Tick, Tick, "ccc_c", Tick>],
  [Event<String><"ddd_dd", Tick, "check", Tick, "This message should not trigger any transition", Tick, "check", Tick>, Event<String><"-> ddd_dd", Tick, "ddd_dd", Tick, Tick, "ddd_dd", Tick>]
}, ticks=[4, 4, 4, 4]>>
component NoEnabledTransitionTest(EventStream<String> input, EventStream<String> expected) {
  NoEnabledTransition sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<String> generator(input);

  AssertEqualsTimed<String> assertions(expected);
}
