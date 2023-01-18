/* (c) https://github.com/MontiCore/monticore */
import subcomponentDefinitions.*;

/**
 * Valid model. (List must be in scope and we allow null)
 */
component CorrectGenericParameterBindings {
  // A field we will use:
  List<int> intList = null;

  Generic<int> correctInstance(4, intList);
}
