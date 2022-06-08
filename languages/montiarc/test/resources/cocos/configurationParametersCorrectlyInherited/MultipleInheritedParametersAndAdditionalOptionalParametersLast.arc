/* (c) https://github.com/MontiCore/monticore */
package configurationParametersCorrectlyInherited;

import configurationParametersCorrectlyInherited.superComponents.*;

/**
 * Valid model.
 */
component MultipleInheritedParametersAndAdditionalOptionalParametersLast (
  String s1, String s2, String s3,
  String sOpt1="sOpt1", String sOpt2="sOpt2", String sOpt3="sOpt3",
  String addSOpt1 = "addSOpt1", String addSOpt2="addSOpt2"
) extends ThreeMandatoryAndThreeOptionalStrings { }