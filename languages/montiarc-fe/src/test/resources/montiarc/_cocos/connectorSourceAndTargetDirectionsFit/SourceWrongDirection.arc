/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetDirectionsFit;

/*
 * Invalid model.
 */
component SourceWrongDirection {
  port out String sOut1, sOut2, sOut3;

  sOut1 -> sOut2; // Error, source of direct port-forward has the wrong direction

  component Inner {
    port in String sIn1, sIn2;
  }

  Inner inner1, inner2;

  sOut1 -> inner1.sIn1; // Error, source of incoming port-forward has the wrong direction
  inner1.sIn2 -> inner2.sIn1; // Error, source of hidden channel has the wrong direction
  inner2.sIn2 -> sOut3; // Error, source of outgoing port-forward has the wrong direction
}