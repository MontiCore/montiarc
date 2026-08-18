/* (c) https://github.com/MontiCore/monticore */
package montiarc.mixed_timing.automata;

import montiarc.types.OnOff;
import montiarc.types.OnOff.*;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitTimed;
import montiarc.maunit.api.EmitSync;

<<test={
  // inA                  inB                  inY             inZ             outA                 outB                 outY            outZ
  [Event<OnOff><Tick>,    Event<OnOff><Tick>,  Sync<ON>,       Sync<ON>,       Event<OnOff><Tick>,  Event<OnOff><Tick>,  Sync<ON>,       Sync<ON>],
  [Event<OnOff><Tick>,    Event<OnOff><Tick>,  Sync<OFF>,      Sync<ON>,       Event<OnOff><Tick>,  Event<OnOff><Tick>,  Sync<OFF>,      Sync<ON>],
  [Event<OnOff><Tick>,    Event<OnOff><Tick>,  Sync<ON>,       Sync<OFF>,      Event<OnOff><Tick>,  Event<OnOff><Tick>,  Sync<ON>,       Sync<OFF>],
  [Event<OnOff><Tick>,    Event<OnOff><Tick>,  Sync<OFF>,      Sync<OFF>,      Event<OnOff><Tick>,  Event<OnOff><Tick>,  Sync<OFF>,      Sync<OFF>],
  [Event<OnOff><OFF, Tick>, Event<OnOff><Tick>, Sync<ON>,      Sync<ON>,       Event<OnOff><OFF, Tick>, Event<OnOff><Tick>, Sync<ON>,    Sync<ON>],
  [Event<OnOff><ON, Tick>, Event<OnOff><Tick>, Sync<OFF>,      Sync<ON>,       Event<OnOff><ON, Tick>, Event<OnOff><Tick>,  Sync<OFF>,   Sync<ON>],
  [Event<OnOff><Tick>,    Event<OnOff><OFF, Tick>, Sync<OFF>,  Sync<ON>,       Event<OnOff><Tick>,  Event<OnOff><OFF, Tick>, Sync<OFF>,  Sync<ON>],
  [Event<OnOff><ON, Tick>, Event<OnOff><OFF, Tick>, Sync<OFF>, Sync<ON>,       Event<OnOff><ON, Tick>, Event<OnOff><OFF, Tick>, Sync<OFF>, Sync<ON>],
  [Event<OnOff><ON, OFF, OFF, Tick>, Event<OnOff><Tick>, Sync<OFF>, Sync<ON>, Event<OnOff><ON, OFF, OFF, Tick>, Event<OnOff><Tick>, Sync<OFF>, Sync<ON>],
  [Event<OnOff><Tick>,    Event<OnOff><ON, OFF, OFF, Tick>, Sync<OFF>, Sync<ON>, Event<OnOff><Tick>, Event<OnOff><ON, OFF, OFF, Tick>, Sync<OFF>, Sync<ON>],
  [Event<OnOff><ON, Tick, Tick, OFF, OFF, ON, Tick>, Event<OnOff><Tick, ON, ON, Tick, ON, Tick>, Sync<OFF, OFF, ON>, Sync<ON, OFF, ON>, Event<OnOff><ON, Tick, Tick, OFF, OFF, ON, Tick>, Event<OnOff><Tick, ON, ON, Tick, ON, Tick>, Sync<OFF, OFF, ON>, Sync<ON, OFF, ON>]
}, ticks=[1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3]>>
component MediumTest(
  EventStream<OnOff> inA,
  EventStream<OnOff> inB,
  SyncStream<OnOff> inY,
  SyncStream<OnOff> inZ,
  EventStream<OnOff> outA,
  EventStream<OnOff> outB,
  SyncStream<OnOff> outY,
  SyncStream<OnOff> outZ
) {
  Medium sut;

  genA.out -> sut.inA;
  genB.out -> sut.inB;
  genY.out -> sut.inY;
  genZ.out -> sut.inZ;

  sut.outA -> assertA.actual;
  sut.outB -> assertB.actual;
  sut.outY -> assertY.actual;
  sut.outZ -> assertZ.actual;

  EmitTimed<OnOff> genA(inA);
  EmitTimed<OnOff> genB(inB);
  EmitSync<OnOff> genY(inY);
  EmitSync<OnOff> genZ(inZ);

  AssertEqualsTimed<OnOff> assertA(outA), assertB(outB);
  AssertEqualsUntimed<OnOff> assertY(outY.untimed()), assertZ(outZ.untimed());
}
