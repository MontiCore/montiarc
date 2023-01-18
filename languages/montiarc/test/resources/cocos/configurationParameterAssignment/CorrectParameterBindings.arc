/* (c) https://github.com/MontiCore/monticore */
import subcomponentDefinitions.*;

/**
 * Valid model.
 */
component CorrectParameterBindings {
  Simple simple(true || false);
  Complex c1(true);
  Complex c2(true, 1 + 1);
  Complex c3(true, 2, 3.2 * 2.3);
}
