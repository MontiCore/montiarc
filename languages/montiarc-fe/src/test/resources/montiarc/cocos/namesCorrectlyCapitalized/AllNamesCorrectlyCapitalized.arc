/* (c) https://github.com/MontiCore/monticore */
package namesCorrectlyCapitalized;

component AllNamesCorrectlyCapitalized<T> (Integer lowerCaseParameter) {

  component UpperCaseInnerComponent { }

  UpperCaseInnerComponent lowerCaseInstance;

  port in Integer lowerCaseIncomingPort;
  port out Integer lowerCaseOutgoingPort;

  Integer lowerCaseVariable;
}