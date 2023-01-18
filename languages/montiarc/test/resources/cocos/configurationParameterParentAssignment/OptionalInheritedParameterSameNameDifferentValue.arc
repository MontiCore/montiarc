/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

/**
 * Valid model.
 */
component OptionalInheritedParameterSameNameDifferentValue(String sOpt1="sOpt1 new value")
  extends configurationParameterParentAssignment.superComponents.OneOptionalString(sOpt1) {
}
