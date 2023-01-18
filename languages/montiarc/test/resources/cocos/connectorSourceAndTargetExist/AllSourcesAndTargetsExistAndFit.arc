/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetExist;

/*
 * Valid model. (Given that List<T> is resolvable)
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
  inner2.listOut -> listOut;
}
