/* (c) https://github.com/MontiCore/monticore */
import subcomponentDefinitions.*;

/**
 * Invalid model.
 */
component TooFewParameterBindings {
  Simple simple1;
  Simple simple2();
  Complex c1;
  Complex c2();
  ComplexWithTwoMandatoryParams c4(c=5.5, b=4);
  ComplexWithTwoMandatoryParams c5(true, c=5.5);
}
