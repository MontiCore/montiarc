package components.head.generics;

import components.body.subcomponents._subcomponents.HasStringInputAndOutput;

/*
 * Invalid model.
 *
 * @implements [Hab16] R15: Components that inherit from a generic
 * component have to assign concrete type arguments to all generic type parameters.
 * (p. 69, lst. 3.50)
 */
component AssignsTypeParamsToSuperCompWithoutFormalParams
            extends HasStringInputAndOutput<String>{
  // Error: HasStringInputAndOutput has no formal type parameters
  // Empty body
}