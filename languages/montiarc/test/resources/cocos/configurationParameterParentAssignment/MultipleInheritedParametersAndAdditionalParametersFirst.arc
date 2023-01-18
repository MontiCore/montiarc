/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

/**
 * Partly valid model. Ordering is invalid but not under testing.
 */
component MultipleInheritedParametersAndAdditionalParametersFirst(int addI, String addSOpt="addSOpt",
   String s1, String s2, String s3, String sOpt1="sOpt1", String sOpt2="sOpt2", String sOpt3="sOpt3")
  extends configurationParameterParentAssignment.superComponents.ThreeMandatoryAndThreeOptionalStrings(s1, s2, s3, sOpt1, sOpt2, sOpt3) {

 }
