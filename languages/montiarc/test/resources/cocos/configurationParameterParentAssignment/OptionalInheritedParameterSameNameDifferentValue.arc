/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

import java.lang.String;

/**
 * Valid model.
 */
component OptionalInheritedParameterSameNameDifferentValue(String sOpt1="sOpt1 new value")
  extends configurationParameterParentAssignment.superComponents.OneOptionalString(sOpt1) {
}
