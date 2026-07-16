/* (c) https://github.com/MontiCore/monticore */
package fmu;

import dahlquist.DahlquistWrapper;
import montiarc.maunit.api.Emit;
import montiarc.maunit.api.AssertEquals;


<<test, simulatedTickLength=1>>
component FeedthroughFmuTest {
  Feedthrough sut(0.0);

  Emit<boolean> emitB(true);
  Emit<double> emitFloat64ContinuousInput(1.5);
  Emit<double> emitFloat64DiscreteInput(2.0);
  Emit<double> emitFloat64TunableParameter(0.5);
  Emit<int> emitInt32Input(42);
  Emit<int> emitEnumerationInput(1);
  Emit<String> emitStringInput("hello");

  emitB.out -> sut.Boolean_input;
  emitFloat64ContinuousInput.out -> sut.Float64_continuous_input;
  emitFloat64DiscreteInput.out -> sut.Float64_discrete_input;
  emitFloat64TunableParameter.out -> sut.Float64_tunable_parameter;
  emitInt32Input.out -> sut.Int32_input;
  emitEnumerationInput.out -> sut.Enumeration_input;
  emitStringInput.out -> sut.String_input;

  sut.Boolean_output -> assertA.actual;
  sut.Float64_continuous_output -> assertFloat64Continuous.actual;
  sut.Float64_discrete_output -> assertFloat64Discrete.actual;
  sut.Int32_output -> assertInt32.actual;
  sut.String_output -> assertString.actual;
  sut.Enumeration_output -> assertEnumeration.actual;

  AssertEquals<boolean> assertA(true);
  AssertEquals<double> assertFloat64Continuous(1.5);
  AssertEquals<double> assertFloat64Discrete(2.0);
  AssertEquals<int> assertInt32(42);
  AssertEquals<String> assertString("hello");
  AssertEquals<int> assertEnumeration(1);
}
