/* (c) https://github.com/MontiCore/monticore */
import subcomponentDefinitions.*;

/**
 * Invalid model.
 */
component WrongTypeParameterBindings {
  Simple simple(4);
  Complex complex1(12);
  Complex complex2(true, 2.3);
  Complex complex3(true, 2.3, 4);
}
