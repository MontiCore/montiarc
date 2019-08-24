/* (c) https://github.com/MontiCore/monticore */
package components.head.generics;

/*
 * Invalid model.
 */
component AssignsNonExistingTypeToSuperComp
    extends GenericComp2<NonExistingType, Integer>{
  // Error: Type 'NonExisting Type does not exist has no formal type parameters
  // Empty body
}
