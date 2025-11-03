/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.Assertions;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=[1,2,3,4], simulatedTickLength=1000000000>>
component DynamicTimerTest(int ticks) {
  EmitTimed<Duration> emitter(<Duration.ofSeconds(ticks), Tick, Tick, Tick, Tick>);
  DynamicTimer sut;

  emitter.out -> sut.start;
  sut.completed -> assert.in;

  component AssertSignalInTicks(int ticks) assert(ticks) {
    port in Signal in;

    int tickExecutions = 0;

    automaton {
      initial state S;
      state Received;
      S -> Received in;
      S -> S / {
        tickExecutions++;
        if (tickExecutions > ticks) Assertions.fail();
      }
    }
  }
}
