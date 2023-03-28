/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetDirectionsFit;

import java.lang.String;
import java.util.List;

/*
 * Valid model.
 */
component AllSourcesAndTargetsExistAndFit {
  port in String sIn;
  port out String sOut;
  port in List<String> listIn;
  port out List<String> listOut;

  component Inner {
    port in String sIn;
    port out String sOut;
    port in List<String> listIn;
    port out List<String> listOut;
  }

  Inner inner1, inner2;

  sIn -> inner1.sIn;
  inner1.sOut -> inner2.sIn;
  inner2.sOut -> sOut;
  listIn -> inner1.listIn;
  inner1.listOut -> inner2.listIn;
  list2.listOut -> listOut;
}
