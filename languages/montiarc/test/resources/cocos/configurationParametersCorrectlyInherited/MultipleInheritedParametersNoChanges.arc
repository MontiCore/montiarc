/* (c) https://github.com/MontiCore/monticore */
package configurationParametersCorrectlyInherited;

component MultipleInheritedParametersNoChanges(String s1, String s2, String s3, String sOpt1="sOpt1",
   String sOpt2="sOpt2", String sOpt3="sOpt3")
  extends configurationParametersCorrectlyInherited.superComponents.ThreeMandatoryAndThreeOptionalStrings {

}