/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

import java.lang.String;

/**
 * Invalid model.
 */
component OptionalInheritedParametersWithSomeTypesChanged(int sOpt1=1, String sOpt2="sOpt2", int sOpt3=3)
  extends configurationParameterParentAssignment.superComponents.ThreeOptionalStrings(sOpt1, sOpt2, sOpt3) {

}
