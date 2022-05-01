/* (c) https://github.com/MontiCore/monticore */
package configurationParametersCorrectlyInherited;

component OptionalInheritedParametersWithSomeTypesChanged(int sOpt1=1, String sOpt2="sOpt2", int sOpt3=3)
  extends configurationParametersCorrectlyInherited.superComponents.ThreeOptionalStrings {

}