/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import montiarc.types.CardinalDirection;
import montiarc.types.CardinalDirection.*;
import montiarc.lang.Signal;
import montiarc.lang.Signal.*;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test={
  // i1                                                      i2                                                 expected
  [Event<CardinalDirection><NORTH, Tick>,                    Event<Signal><Tick, SIGNAL>,                       Event<CardinalDirection><Tick, NORTH>],
  [Event<CardinalDirection><NORTH, Tick>,                    Event<Signal><Tick>,                               Event<CardinalDirection><Tick>],
  [Event<CardinalDirection><Tick>,                           Event<Signal><Tick, SIGNAL>,                       Event<CardinalDirection><Tick>],
  [Event<CardinalDirection><NORTH, Tick>,                    Event<Signal><Tick, SIGNAL, SIGNAL>,               Event<CardinalDirection><Tick, NORTH>],
  [Event<CardinalDirection><NORTH, NORTH, Tick>,             Event<Signal><Tick, SIGNAL>,                       Event<CardinalDirection><Tick, NORTH>],
  [Event<CardinalDirection><NORTH, NORTH, Tick>,             Event<Signal><Tick, SIGNAL, SIGNAL>,               Event<CardinalDirection><Tick, NORTH, NORTH>],
  [Event<CardinalDirection><NORTH, EAST, Tick>,              Event<Signal><Tick, SIGNAL, SIGNAL>,               Event<CardinalDirection><Tick, EAST, NORTH>],
  [Event<CardinalDirection><NORTH, Tick, EAST, Tick>,        Event<Signal><Tick, Tick, SIGNAL, SIGNAL>,         Event<CardinalDirection><Tick, Tick, EAST, NORTH>],
  [Event<CardinalDirection><NORTH, Tick, Tick, EAST, Tick>,  Event<Signal><Tick, SIGNAL, Tick, Tick, SIGNAL>,   Event<CardinalDirection><Tick, NORTH, Tick, Tick, EAST>],
  [Event<CardinalDirection><NORTH, EAST, SOUTH, WEST, Tick>, Event<Signal><Tick, SIGNAL, SIGNAL, SIGNAL, SIGNAL>, Event<CardinalDirection><Tick, WEST, SOUTH, EAST, NORTH>]
}, ticks=[1, 1, 1, 1, 1, 1, 1, 2, 3, 1]>>
component List1Test(
  EventStream<CardinalDirection> i,
  EventStream<Signal> i2,
  EventStream<CardinalDirection> expected
) {
  List1 sut;

  gen1.out -> sut.i;
  gen2.out -> sut.i2;
  sut.o -> assertions.actual;

  EmitTimed<CardinalDirection> gen1(i);
  EmitTimed<Signal> gen2(i2);

  AssertEqualsTimed<CardinalDirection> assertions(expected);
}
