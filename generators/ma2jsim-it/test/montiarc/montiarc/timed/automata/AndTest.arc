/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test,
  ticks=3,
  a=[
    Event<Boolean><false, false, Tick, true, Tick, true, Tick>,
    Event<Boolean><false, false, Tick, false, Tick, false, true, Tick>
  ],
  b=[
    Event<Boolean><false, true, Tick, false, Tick, true, Tick>,
    Event<Boolean><false, true, Tick, false, Tick, true, true, Tick>
  ],
  expected=[
    Event<Boolean><false, Tick, false, Tick, true, Tick>,
    Event<Boolean><false, Tick, false, Tick, true, Tick>
  ]>>
  component AndTest(EventStream<Boolean> a,
                    EventStream<Boolean> b,
                    EventStream<Boolean> expected) {

  And sut;

  emitterA.out -> sut.a;
  emitterB.out -> sut.b;
  sut.q -> assertEquals.actual;

  AssertEqualsTimed<Boolean> assertEquals(expected);
  EmitTimed<Boolean> emitterA(a);
  EmitTimed<Boolean> emitterB(b);
}
