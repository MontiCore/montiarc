/* (c) https://github.com/MontiCore/monticore */
import subcomponentDefinitions.*;

/**
 * Invalid model. For testing purposes, let the type 'Person' be resolvable.
 */
component TooFewParameterBindingsAndTypeRef {
  ComplexWithTwoMandatoryParams complex(Person);
}
