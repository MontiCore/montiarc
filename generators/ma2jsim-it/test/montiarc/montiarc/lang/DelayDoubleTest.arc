/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.lang.Double;

<<test, ticks=[1,1,1,1,1,1,2,2,2,2], input=[
  <Double.MIN_VALUE>,
  <Double.MAX_VALUE>,
  <Double.MIN_VALUE, Double.MIN_VALUE>,
  <Double.MIN_VALUE, Double.MAX_VALUE>,
  <Double.MAX_VALUE, Double.MIN_VALUE>,
  <Double.MAX_VALUE, Double.MAX_VALUE>,
  <Double.MIN_VALUE, Tick, Double.MIN_VALUE>,
  <Double.MIN_VALUE, Tick, Double.MAX_VALUE>,
  <Double.MAX_VALUE, Tick, Double.MIN_VALUE>,
  <Double.MAX_VALUE, Tick, Double.MAX_VALUE>
], output=[
  <Tick, Double.MIN_VALUE>,
  <Tick, Double.MAX_VALUE>,
  <Tick, Double.MIN_VALUE, Double.MIN_VALUE>,
  <Tick, Double.MIN_VALUE, Double.MAX_VALUE>,
  <Tick, Double.MAX_VALUE, Double.MIN_VALUE>,
  <Tick, Double.MAX_VALUE, Double.MAX_VALUE>,
  <Tick, Double.MIN_VALUE, Tick, Double.MIN_VALUE>,
  <Tick, Double.MIN_VALUE, Tick, Double.MAX_VALUE>,
  <Tick, Double.MAX_VALUE, Tick, Double.MIN_VALUE>,
  <Tick, Double.MAX_VALUE, Tick, Double.MAX_VALUE>
]>>
component DelayDoubleTest(EventStream<double> input, EventStream<double> output) {
  DelayDouble sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<double> generator(input);

  AssertEqualsTimed<double> assertions(output);
}
