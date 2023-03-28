/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

import java.lang.String;

/**
 * Valid model.
 */
component OptionalInheritedParameterNoChanges(String sOpt1="sOpt1")
  extends configurationParameterParentAssignment.superComponents.OneOptionalString(sOpt1) {

}
