/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetExist;

import java.lang.String;

/*
 * Invalid model.
 */
component SourceDoesNotExist {
  port out String sOut, sOut2;

  missingPort -> sOut; // Error, source of direct port-forward does not exist

  component Inner {
    port in String sIn;
  }

  Inner inner1, inner2;

  missingPort -> inner1.sIn; // Error, source of incoming port-forward does not exist
  inner1.missingPort -> inner2.sIn; // Error, source of hidden channel does not exist
  inner2.missingPort -> sOut; // Error, source of outgoing port-forward does not exist

  noInner.foo -> sOut2; // Error, noInner is no subcomponent
}
