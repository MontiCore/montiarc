/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

import java.lang.String;

/**
 * Invalid model.
 */
component MandatoryInheritedParametersUnordered(int i1, String s2)
  extends configurationParameterParentAssignment.superComponents.OneMandatoryStringAndOneMandatoryInt(i1, s2) {

}
