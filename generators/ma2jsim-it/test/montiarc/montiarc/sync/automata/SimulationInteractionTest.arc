/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.lang.Duration;
import montiarc.maunit.api.AssertEqualsTimed;

<<test, ticks=10, simulatedTickLength=1000000, expected=<Duration.ofMilliseconds(1), Tick>>>
component SimulationInteractionTest(EventStream<Duration> expected) {
  SimulationInteraction sut;

  sut.o -> assertions.actual;

  AssertEqualsTimed<Duration> assertions(expected);
}
