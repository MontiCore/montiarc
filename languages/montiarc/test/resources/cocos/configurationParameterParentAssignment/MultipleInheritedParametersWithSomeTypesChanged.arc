/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

/**
 * Invalid model.
 */
component MultipleInheritedParametersWithSomeTypesChanged(String s1, int s2, String s3, String sOpt1="sOpt1",
   int sOpt2=2, String sOpt3="sOpt3")
  extends configurationParameterParentAssignment.superComponents.ThreeMandatoryAndThreeOptionalStrings(s1, s2, s3, sOpt1, sOpt2, sOpt4) {

}