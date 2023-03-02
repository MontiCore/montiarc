/* (c) https://github.com/MontiCore/monticore */
import subcomponentDefinitions.*;

/**
 * Valid model.
 */
component CorrectParameterBindings(boolean a) {
  // A field we will use:
  boolean b = false;

  Simple simple(a || b);
  Simple simple2(a=true || false);
  Complex c1(a=a);
  Complex c2(a=b);
  Complex c3(true, b=1 + 1);
  Complex c4(true, 2, 3.2 * 2.3);
}
