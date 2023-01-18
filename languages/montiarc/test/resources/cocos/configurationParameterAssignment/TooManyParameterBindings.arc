/* (c) https://github.com/MontiCore/monticore */
import subcomponentDefinitions.*;

/**
 * Invalid model.
 */
component TooManyParameterBindings {
  Simple simple1(true, 2);
  Complex complex(true, 2, 2.3, false);
}
