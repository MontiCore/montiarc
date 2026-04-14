/* (c) https://github.com/MontiCore/monticore */
package components;

/**
 * Invalid model: The type arguments boolean and Boolean do not respect
 * the upper bounds of the corresponding type parameters.
 * Bar1 uses boolean although Foo1 requires a type argument extending int.
 * Bar2 uses Boolean although Foo2 requires a type argument extending Integer.
 */
component PrimitiveTypeParameterBounds {

  component Foo1<T extends int> {}
  component Bar1 extends Foo1<boolean> {}

  component Foo2<T extends Integer> {}
  component Bar2 extends Foo2<Boolean> {}
}
