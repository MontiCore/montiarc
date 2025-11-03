/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.gate;

import montiarc.maunit.api.EmitSync;
import montiarc.maunit.api.AssertEqualsUntimed;

<<test, ticks=[4, 5], a=[
 Sync<false, false, true, true>,
 Sync<false, false, false, false, true>
], b=[
 Sync<false, true, false, true>,
 Sync<false, true, false, true, true>
], expected=[
 Untimed<false, false, false, true>,
 Untimed<false, false, false, false, true>
]>>
component AndTest(SyncStream<boolean> a, SyncStream<boolean> b,
 UntimedStream<boolean> expected) {
 And sut;

 emitterA.out -> sut.a;
 emitterB.out -> sut.b;
 sut.q -> assertEquals.actual;

 AssertEqualsUntimed<boolean> assertEquals(expected);
 EmitSync<boolean> emitterA(a);
 EmitSync<boolean> emitterB(b);
}
