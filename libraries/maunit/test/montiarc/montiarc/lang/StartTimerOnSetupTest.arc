/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.Assertions;

<<test, ticks=4, simulatedTickLength=1000000000>>
component StartTimerOnSetupTest(long simulatedTickLength) {
  Timer timer = Timer.start(Duration.ofMilliseconds(simulatedTickLength*4/1000000));
  int tickExecutions = 0;

  compute {
    tickExecutions++;
    Assertions.assertEquals((4 - tickExecutions) * simulatedTickLength / 1000000, timer.remaining().getInMilliseconds());
  }
}
