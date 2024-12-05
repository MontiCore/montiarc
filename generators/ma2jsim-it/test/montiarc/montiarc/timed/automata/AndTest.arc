package montiarc.timed.automata;

import java.util.List;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test,
  ticks=3,
  a=[
    [[false, false], [true], [true]],
    [[false, false], [false], [false, true]]
  ],
  b=[
    [[false, true], [false], [true]],
    [[false, true], [false], [true, true]]
  ],
  expected=[
    [[false], [false], [true]],
    [[false], [false], [true]]
  ]>>
  component AndTest(List<List<Boolean>> a,
                    List<List<Boolean>> b,
                    List<List<Boolean>> expected) {

  And sut;

  emitterA.out -> sut.a;
  emitterB.out -> sut.b;
  sut.q -> assertEquals.actual;

  AssertEqualsTimed<Boolean> assertEquals(expected);
  EmitTimed<Boolean> emitterA(a);
  EmitTimed<Boolean> emitterB(b);
}