/* (c) https://github.com/MontiCore/monticore */
package configurationParametersCorrectlyInherited;

component MultipleInheritedParametersWithSomeTypesChanged(String s1, int s2, String s3, String sOpt1="sOpt1",
   int sOpt2=2, String sOpt3="sOpt3")
  extends configurationParametersCorrectlyInherited.superComponents.ThreeMandatoryAndThreeOptionalStrings {

}