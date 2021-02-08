/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetExistAndFit;

component TargetDoesNotExist {
  port in String sIn;
  sIn -> nonExistentTarget;
}