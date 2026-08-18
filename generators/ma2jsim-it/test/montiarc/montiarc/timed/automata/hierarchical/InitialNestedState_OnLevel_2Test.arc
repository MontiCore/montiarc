/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.hierarchical;

import montiarc.maunit.api.AssertEqualsUntimed;

<<test, ticks=1, expected=[Untimed<String><"AAA">]>>
component InitialNestedState_OnLevel_2Test(UntimedStream<String> expected) {
  InitialNestedState_OnLevel_2 sut;

  sut.o -> assertions.actual;

  AssertEqualsUntimed<String> assertions(expected);
}
