/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetTypesFit;

import java.lang.Integer;
import java.lang.String;

/*
 * Invalid model.
 */
component SourceAndTargetTypeMismatch {
  port in String sIn;
  port out Integer sOut;

  sIn -> sOut; // Error, type of source and target of direct port-forward mismatch

  component Inner {
    port in Integer  sIn;
    port out String sOut;
  }

  Inner inner1, inner2;

  sIn -> inner1.sIn; // Error, type of source and target of incoming port-forward mismatch
  inner1.sOut -> inner2.sIn; // Error, type of source and target of hidden channel mismatch
  inner2.sOut -> sOut; // Error, type of source and target of outgoing port-forward mismatch
}
