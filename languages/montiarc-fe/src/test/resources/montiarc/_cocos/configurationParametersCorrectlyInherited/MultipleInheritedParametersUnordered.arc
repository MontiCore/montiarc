/* (c) https://github.com/MontiCore/monticore */
package configurationParametersCorrectlyInherited;

component MultipleInheritedParametersUnordered(String s3, String s1, String sOpt3="sOpt3", String s2,
   String sOpt1="sOpt1", String sOpt2="sOpt2")
  extends configurationParametersCorrectlyInherited.superComponents.ThreeMandatoryAndThreeOptionalStrings {

}