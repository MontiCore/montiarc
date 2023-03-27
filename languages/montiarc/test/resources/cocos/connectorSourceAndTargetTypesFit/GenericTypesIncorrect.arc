/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetTypesFit;

import connectorSourceAndTargetTypesFit.subcomponentDefinitions.*;
import java.util.List;

/*
 * Invalid model.
 */
component GenericTypesIncorrect<B> {
  port in int intIn;
  port out int intOut;
  port in List<boolean> boolListIn;
  port out List<boolean> boolListOut;
  port out List<B> bListOut;

  Generic<int> innerInt;

  intIn -> innerInt.aIn;
  innerInt.aOut -> intOut;
  boolListIn -> innerInt.aListIn;   // List<booelan> != List<int>
  innerInt.aListOut -> boolListOut; // List<int> != List<boolean>
  innerInt.aListOut -> bListOut;    // List<int> != List<B>
}
