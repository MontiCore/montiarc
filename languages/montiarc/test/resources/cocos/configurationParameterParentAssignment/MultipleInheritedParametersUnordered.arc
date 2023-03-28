/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

import java.lang.String;

/**
 * Valid model.
 */
component MultipleInheritedParametersUnordered(String s3, String s1, String sOpt3="sOpt3", String s2,
   String sOpt1="sOpt1", String sOpt2="sOpt2")
  extends configurationParameterParentAssignment.superComponents.ThreeMandatoryAndThreeOptionalStrings(s1, s2, s3, sOpt1, sOpt2, sOpt3) {

}
