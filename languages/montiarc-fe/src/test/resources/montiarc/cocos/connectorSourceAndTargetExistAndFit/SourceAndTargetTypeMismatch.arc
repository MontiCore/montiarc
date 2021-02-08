/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetExistAndFit;

component SourceAndTargetTypeMismatch {
  port in String sIn;
  port out int iOut;
  sIn -> iOut;
}