/* (c) https://github.com/MontiCore/monticore */
import subcomponentDefinitions.*;

/**
 * Invalid model. For testing purposes, let the type 'Person' be resolvable.
 */
component IncompatibleAndBindsTypeRef {
  Complex c1(2 + 3, Person);
}
