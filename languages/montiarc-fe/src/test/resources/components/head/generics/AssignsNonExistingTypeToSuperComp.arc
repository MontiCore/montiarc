package components.head.generics;

import types.GenericComp2;

/*
 * Invalid model.
 */
component AssignsNonExistingTypeToSuperComp
    extends GenericComp2<NonExistingType, Integer>{
  // Error: Type 'NonExisting Type does not exist has no formal type parameters
  // Empty body
}