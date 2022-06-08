/* (c) https://github.com/MontiCore/monticore */

import subcomponentDefinitions.*;

/**
 * Invalid model. (List must be in scope and we allow null)
 */
component WrongGenericParameterBindings {
  // Fields we will use:
  List<boolean> boolList = null;
  List<int> intList = null;

  Generic<int> wrongInt(true, intList);
  Generic<int> wrongList(4, boolList);

}