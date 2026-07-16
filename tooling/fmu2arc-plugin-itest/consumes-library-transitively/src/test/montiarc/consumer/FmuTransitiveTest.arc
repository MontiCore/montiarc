/* (c) https://github.com/MontiCore/monticore */
package consumer;

import montiarc.maunit.api.Emit;
import montiarc.maunit.api.AssertEquals;

<<test, simulatedTickLength=2>>
component FmuTransitiveTest {

  FmuConsumer sut;

  Emit<boolean> emitB(true);
  Emit<double>  emitContinuous(1.5);
  Emit<double>  emitDiscrete(2.0);
  Emit<double>  emitTunable(0.5);
  Emit<int>     emitInt32(42);
  Emit<int>     emitEnum(1);
  Emit<String>  emitString("hello");

  emitB.out         -> sut.boolIn;
  emitContinuous.out -> sut.continuousIn;
  emitDiscrete.out  -> sut.discreteIn;
  emitTunable.out   -> sut.tunableParam;
  emitInt32.out     -> sut.int32In;
  emitEnum.out      -> sut.enumIn;
  emitString.out    -> sut.stringIn;

  sut.boolOut       -> assertBool.actual;
  sut.continuousOut -> assertContinuous.actual;
  sut.discreteOut   -> assertDiscrete.actual;
  sut.int32Out      -> assertInt32.actual;
  sut.stringOut     -> assertString.actual;
  sut.enumOut       -> assertEnum.actual;

  AssertEquals<boolean> assertBool(true);
  AssertEquals<double>  assertContinuous(1.5);
  AssertEquals<double>  assertDiscrete(2.0);
  AssertEquals<int>     assertInt32(42);
  AssertEquals<String>  assertString("hello");
  AssertEquals<int>     assertEnum(1);
}
