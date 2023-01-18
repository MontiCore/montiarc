/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetTypesFit;

import connectorSourceAndTargetTypesFit.subcomponentDefinitions.*;

/*
 * Valid model. (Given that List<T> is resolvable)
 */
component GenericTypesCorrect<B> {
  port in int intIn;
  port out int intOut;
  port in List<int> listIntIn;
  port out List<int> intListOut;

  port in B bIn;
  port out B bOut;
  port in List<B> bListIn;
  port out List<B> bListOut;

  Generic<int> innerInt;
  Generic<B> innerB;

  intIn -> innerInt.aIn;
  intListIn -> innerInt.aListIn;
  innerInt.aListOut -> intListOut;
  innerInt.aOut -> intOut;

  bIn -> innerB.aIn;
  bListIn -> innerB.aListIn;
  innerB.aListOut -> bListOut;
  innerB.aOut -> bOut;
}
