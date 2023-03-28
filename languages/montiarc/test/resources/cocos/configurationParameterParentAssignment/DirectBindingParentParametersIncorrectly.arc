/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

import configurationParameterParentAssignment.superComponents.*;
import java.lang.String;

/**
 * Invalid model.
 */
component DirectBindingParentParametersIncorrectly extends ThreeMandatoryAndThreeOptionalStrings (
  s1,   // symbol does not exist #TODO No error logged
  true, // wrong type
  2,    // wrong type
  "sOpt1",
  3    // wrong type
) { }
