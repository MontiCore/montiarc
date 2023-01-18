/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

/**
 * Valid model.
 */
component MandatoryInheritedParameterBecomesOptionalSameName(String s1="s1")
  extends configurationParameterParentAssignment.superComponents.OneMandatoryString(s1) {

}
