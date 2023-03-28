/* (c) https://github.com/MontiCore/monticore */
package namesCorrectlyCapitalized;

import java.lang.Integer;
import java.lang.String;

component allNamesIncorrectlyCapitalized<t> (Integer UpperCaseParameter) {

  component lowerCaseInnerComponent { }

  lowerCaseInnerComponent UpperCaseInstance;

  port in Integer UpperCaseIncomingPort;
  port out Integer UpperCaseOutgoingPort;

  Integer UpperCaseVariable = 42;

  feature UpperCaseFeature;
}
