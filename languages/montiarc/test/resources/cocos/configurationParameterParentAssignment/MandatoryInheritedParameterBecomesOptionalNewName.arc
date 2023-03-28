/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

import java.lang.String;

/**
 * Valid model.
 */
component MandatoryInheritedParameterBecomesOptionalNewName(String sOpt1="s1")
  extends configurationParameterParentAssignment.superComponents.OneMandatoryString(sOpt1) {

}
