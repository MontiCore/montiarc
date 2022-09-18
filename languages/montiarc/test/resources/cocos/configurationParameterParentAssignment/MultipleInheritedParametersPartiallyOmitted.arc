/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

/**
 * Valid model.
 */
component MultipleInheritedParametersPartiallyOmitted(String s1, String s2, String sOpt1="sOpt1", String sOpt2="sOpt2")
  extends configurationParameterParentAssignment.superComponents.ThreeMandatoryAndThreeOptionalStrings(s1, s2, sOpt1, sOpt2) {

}
