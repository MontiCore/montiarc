/* (c) https://github.com/MontiCore/monticore */
import subcomponentDefinitions.*;

import java.util.List;

/**
 * Valid model.
 */
component CorrectGenericParameterBindings {
  // A field we will use:
  List<int> intList = null;

  Generic<int> correctInstance(4, aList=intList);
}
