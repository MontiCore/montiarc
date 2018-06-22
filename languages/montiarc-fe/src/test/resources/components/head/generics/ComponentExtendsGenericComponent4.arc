package components.head.generics;

/*
 * Invalid model.
 *
 * @implements [Hab16] R15: Components that inherit from a generic component
 *  have to assign concrete type arguments to all generic type parameters.
 *  (p. 69, lst. 3.50)
 */
component ComponentExtendsGenericComponent4 extends types.GenericComp2 {
  // ERROR: All generic types of super component 'types.GenericComp2' have
  //    to be assigned.
  // Empty body
}