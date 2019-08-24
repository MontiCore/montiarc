/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

import components.body.subcomponents._subcomponents.CompWithGenericArg;
import types.Datatypes.TimerSignal;

/**
 * Invalid model. Buffer expects a String parameter.
 *
 * @implements [Wor16] MT7: Default values of parameters conform to their
 *  type. (p. 64 Lst. 4.22)
 */ 
component SubcomponentParametersOfWrongType {

  component Buffer(5) subcomp1; // Wrong argument
  component Buffer("foo") subcomp2;

  component Buffer(TimerSignal.ALERT) subcomp1; // Wrong argument
  component Buffer("foo") subcomp2;
  
  component CompWithGenericArg<Integer>("Foo") subcomp4; // Wrong argument
  component CompWithGenericArg<String>("Foo") subcomp3;
  
}
