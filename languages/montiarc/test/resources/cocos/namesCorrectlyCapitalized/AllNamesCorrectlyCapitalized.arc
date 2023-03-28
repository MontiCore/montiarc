/* (c) https://github.com/MontiCore/monticore */
package namesCorrectlyCapitalized;

import java.lang.Integer;
import java.lang.String;

component AllNamesCorrectlyCapitalized<T> (Integer lowerCaseParameter) {

  component UpperCaseInnerComponent { }

  UpperCaseInnerComponent lowerCaseInstance;

  port in Integer lowerCaseIncomingPort;
  port out Integer lowerCaseOutgoingPort;

  Integer lowerCaseVariable = lowerCaseParameter;

  feature lowerCaseFeature;
}
