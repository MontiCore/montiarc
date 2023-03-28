/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

import java.lang.String;

/**
 * Valid model.
 */
component OptionalInheritedParameterDifferentNameDifferentValue(String sOpt="sOpt")
  extends configurationParameterParentAssignment.superComponents.OneOptionalString(sOpt) {

}
