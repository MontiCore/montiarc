/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetExistAndFit;

/*
 * Valid model.
 */
component AllSourcesAndTargetsExistAndFit {
  port in String sIn;
  port out String sOut;

  component Inner {
    port in String sIn;
    port out String sOut;
  }

  Inner inner1, inner2;

  sIn -> inner1.sIn;
  inner1.sOut -> inner2.sIn;
  inner2.sOut -> sOut;
}