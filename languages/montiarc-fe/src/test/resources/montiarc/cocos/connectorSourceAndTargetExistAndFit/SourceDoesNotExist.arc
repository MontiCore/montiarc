/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetExistAndFit;

component SourceDoesNotExist {
  port out String sOut;
  nonExistentSource -> sOut;
}