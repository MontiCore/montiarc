package components.head.generics;

/*
 * Invalid model.
 */
component ComponentExtendsGenericComponent6<X,Y> extends types.GenericComp2<X, Y> {
  // ERROR: Type incompatibility of generic type 'Y': Not a subclass of Number.
  // Empty body
}