package components.head.generics;

/*
 * Invalid model.
 *
 * @implements [Hab16] R15: Components that inherit from a generic component
 * have to assign concrete type arguments to all generic type parameters.
 * (p.69, lst. 3.50)
 */
component ComponentExtendsGenericComponent6<X,Y> extends GenericComp2<X, Y> {
  // ERROR: Type incompatibility of generic type 'Y': Not a subclass of Number.
  // Empty body
}