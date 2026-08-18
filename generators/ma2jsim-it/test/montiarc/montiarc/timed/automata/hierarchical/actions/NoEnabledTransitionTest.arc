/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical.actions;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test={
  [Event<String><"aa", "No enabled transition", Tick, "No enabled transition", "No enabled transition", Tick, Tick>, Event<String><"INIT -> aa", "-> a", "-> aa", "~ a", "~ aa", Tick, "~ a", "~ aa", Tick, "~ a", "~ aa", Tick>],
  [Event<String><"bbb", "No enabled transition", Tick, "No enabled transition", "No enabled transition", Tick, Tick>, Event<String><"INIT -> bbb", "-> b", "-> bb", "-> bbb", "~ b", "~ bb", "~ bbb", Tick, "~ b", "~ bb", "~ bbb", Tick, "~ b", "~ bb", "~ bbb", Tick>],
  [Event<String><"ccc_c", "No enabled transition", Tick, "No enabled transition", "No enabled transition", Tick, Tick>, Event<String><"INIT -> ccc_c", "-> c", "-> cc", "-> ccc", "-> ccc_c", "~ c", "~ cc", "~ ccc", "~ ccc_c", Tick, "~ c", "~ cc", "~ ccc", "~ ccc_c", Tick, "~ c", "~ cc", "~ ccc", "~ ccc_c", Tick>],
  [Event<String><"ddd_dd", "No enabled transition", Tick, "No enabled transition", "No enabled transition", Tick, Tick>, Event<String><"INIT -> ddd_dd", "-> d", "-> dd", "-> ddd", "-> ddd_d", "-> ddd_dd", "~ d", "~ dd", "~ ddd", "~ ddd_d", "~ ddd_dd", Tick, "~ d", "~ dd", "~ ddd", "~ ddd_d", "~ ddd_dd", Tick, "~ d", "~ dd", "~ ddd", "~ ddd_d", "~ ddd_dd", Tick>]
}, ticks=[3, 3, 3, 3]>>
component NoEnabledTransitionTest(EventStream<String> input, EventStream<String> expected) {
  NoEnabledTransition sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<String> generator(input);

  AssertEqualsTimed<String> assertions(expected);
}
