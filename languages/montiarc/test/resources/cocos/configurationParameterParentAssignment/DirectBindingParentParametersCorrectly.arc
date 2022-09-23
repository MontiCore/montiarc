/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

import configurationParameterParentAssignment.superComponents.*;

/**
 * Valid model.
 */
component DirectBindingParentParametersCorrectly extends ThreeMandatoryAndThreeOptionalStrings (
  "s1", "s2", "s3", "sOpt1", "sOpt2", "sOpt3"
) { }