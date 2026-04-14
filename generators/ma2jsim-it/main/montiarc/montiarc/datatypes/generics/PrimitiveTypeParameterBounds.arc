/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

component PrimitiveTypeParameterBounds {
  component Foo1<T extends int> {}
  component Bar1 extends Foo1<short> {}

  component Foo2<T extends Integer> {}
  component Bar2 extends Foo2<Short> {}
}
