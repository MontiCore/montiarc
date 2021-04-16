/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetExistAndFit;

component SourceWrongDirection {
  port out String sOut1, sOut2;
  sOut1 -> sOut2;
}