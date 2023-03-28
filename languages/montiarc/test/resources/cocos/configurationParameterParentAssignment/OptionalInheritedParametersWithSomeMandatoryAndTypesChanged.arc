/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

import java.lang.String;

/**
 * Invalid model.
 */
component OptionalInheritedParametersWithSomeMandatoryAndTypesChanged(int sOpt1=1, String sOpt2="sOpt2", int i3)
  extends configurationParameterParentAssignment.superComponents.ThreeOptionalStrings(sOpt1, sOpt2, i3) {

}
