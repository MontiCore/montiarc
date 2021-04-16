/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetExistAndFit;

component SourceAndTargetWrongDirection {
  port in String sIn;
  port out String sOut;
  sOut -> sIn;
}