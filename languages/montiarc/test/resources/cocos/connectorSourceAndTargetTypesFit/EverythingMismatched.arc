/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetTypesFit;

/*
 * Invalid model.
 */
component EverythingMismatched {
  port in String sIn;
  port out Integer sOut;

  sOut -> sIn; // Error, source and target of direct port-forward have mismatched types (and the wrong direction)

  component Inner {
    port in Integer sIn;
    port out String sOut;
  }

  Inner inner1, inner2;

  sOut -> inner1.sOut; // Error, source and target of incoming port-forward have mismatched types (and the wrong direction)
  inner1.sIn -> inner2.sOut; // Error, source and target of hidden channel have mismatched types (and the wrong direction)
  inner2.sIn -> sIn; // Error, source and target of outgoing port-forward have mismatched types (and the wrong direction)
}
