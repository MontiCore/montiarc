/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

import java.lang.String;

/**
 * Valid model.
 */
component OptionalInheritedParametersUnordered(String sOpt3="sOpt3", String sOpt1="sOpt1", String sOpt2="sOpt2")
  extends configurationParameterParentAssignment.superComponents.ThreeOptionalStrings(sOpt1, sOpt2, sOpt3) {

}
