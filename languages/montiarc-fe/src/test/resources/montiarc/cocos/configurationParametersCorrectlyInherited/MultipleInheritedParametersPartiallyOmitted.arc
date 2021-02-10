/* (c) https://github.com/MontiCore/monticore */
package configurationParametersCorrectlyInherited;

component MultipleInheritedParametersPartiallyOmitted(String s1, String s2, String sOpt1="sOpt1", String sOpt2="sOpt2")
  extends configurationParametersCorrectlyInherited.superComponents.ThreeMandatoryAndThreeOptionalStrings {

}
