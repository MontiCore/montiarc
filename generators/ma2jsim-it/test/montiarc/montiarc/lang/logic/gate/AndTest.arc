/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.gate;

import montiarc.maunit.api.EmitSync;
import montiarc.maunit.api.AssertEqualsUntimed;
import java.util.List;

<<test, ticks=[4, 5], a=[
 [false, false, true, true],
 [false, false, false, false, true]
], b=[
 [false, true, false, true],
 [false, true, false, true, true]
], expected=[
 [false, false, false, true],
 [false, false, false, false, true]
]>>
component AndTest(List<boolean> a, List<boolean> b,
 List<boolean> expected) {
 And sut;

 emitterA.out -> sut.a;
 emitterB.out -> sut.b;
 sut.q -> assertEquals.actual;

 AssertEqualsUntimed<boolean> assertEquals(expected);
 EmitSync<boolean> emitterA(a);
 EmitSync<boolean> emitterB(b);
}