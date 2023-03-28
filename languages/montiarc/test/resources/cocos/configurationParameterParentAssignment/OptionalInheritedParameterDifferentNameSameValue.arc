/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

import java.lang.String;

/**
 * Valid model.
 */
component OptionalInheritedParameterDifferentNameSameValue(String sOpt="sOpt1")
  extends configurationParameterParentAssignment.superComponents.OneOptionalString(sOpt) {

}
