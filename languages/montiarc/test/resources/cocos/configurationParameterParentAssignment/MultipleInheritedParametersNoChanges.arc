/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

/**
 * Valid model.
 */
component MultipleInheritedParametersNoChanges(String s1, String s2, String s3, String sOpt1="sOpt1",
   String sOpt2="sOpt2", String sOpt3="sOpt3")
  extends configurationParameterParentAssignment.superComponents.ThreeMandatoryAndThreeOptionalStrings(s1, s2, s3, sOpt1, sOpt2, sOpt3) {

}