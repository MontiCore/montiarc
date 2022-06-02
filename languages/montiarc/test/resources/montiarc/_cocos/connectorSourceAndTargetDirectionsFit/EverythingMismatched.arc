/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetDirectionsFit;

/*
 * Invalid model.
 */
component EverythingMismatched {
  port in String sIn;
  port out Integer sOut;

  sOut -> sIn; // Error, source and target of direct port-forward have the wrong direction (and types mismatch)

  component Inner {
    port in Integer sIn;
    port out String sOut;
  }

  Inner inner1, inner2;

  sOut -> inner1.sOut; // Error, source and target of incoming port-forward have the wrong direction (and types mismatch)
  inner1.sIn -> inner2.sOut; // Error, source and target of hidden channel have the wrong direction (and types mismatch)
  inner2.sIn -> sIn; // Error, source and target of outgoing port-forward have the wrong direction (and types mismatch)
}