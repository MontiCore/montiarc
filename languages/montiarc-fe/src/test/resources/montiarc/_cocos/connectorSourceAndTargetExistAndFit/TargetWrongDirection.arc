/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetExistAndFit;

/*
 * Invalid model.
 */
component TargetWrongDirection {
  port in String sIn1, sIn2, sIn3;

  sIn1 -> sIn2; // Error, target of direct port-forward has the wrong direction

  component Inner {
    port out String sOut1, sOut2;
  }

  Inner inner1, inner2;

  sIn1 -> inner1.sOut1; // Error, target of incoming port-forward has the wrong direction
  inner1.sOut2 -> inner2.sOut1; // Error, target of hidden channel has the wrong direction
  inner2.sOut2 -> sIn3; // Error, target of outgoing port-forward has the wrong direction
}