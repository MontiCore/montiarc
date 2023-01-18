/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

/**
 * Invalid model.
 */
component OptionalInheritedParameterWithTypeChange(int sOpt1=1)
  extends configurationParameterParentAssignment.superComponents.OneOptionalString(sOpt1) {
}
