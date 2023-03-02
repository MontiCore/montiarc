/* (c) https://github.com/MontiCore/monticore */
import subcomponentDefinitions.*;

/**
 * Invalid model.
 */
component TooFewAndWrongBindings {
  ComplexWithTwoMandatoryParams c1(5.2);
  ComplexWithTwoMandatoryParams c2(c=false);
  ComplexWithTwoMandatoryParams c3(b=4.2,c=false);
}
