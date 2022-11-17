/* (c) https://github.com/MontiCore/monticore */
package namesCorrectlyCapitalized;

component allNamesIncorrectlyCapitalized<t> (Integer UpperCaseParameter) {

  component lowerCaseInnerComponent { }

  lowerCaseInnerComponent UpperCaseInstance;

  port in Integer UpperCaseIncomingPort;
  port out Integer UpperCaseOutgoingPort;

  Integer UpperCaseVariable = 42;

  feature UpperCaseFeature;
}