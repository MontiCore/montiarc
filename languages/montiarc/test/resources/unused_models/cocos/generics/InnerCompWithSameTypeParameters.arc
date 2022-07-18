/* (c) https://github.com/MontiCore/monticore */
package components.head.generics;

/*
 * Invalid model. Generic type parameters may not be reused in inner components.
 *
 * @implements [Hab16] B1: All names of model elements within a component namespace have to be unique. (p. 59, Listing 3.31)
 */
component InnerCompWithSameTypeParameters<T> {

  component Inner<U,T> { // Error: T

  }

  component Inner<String, String> inner;

  component InnerWithInner<V> {

      component InnerInner<U, T> { // Error: T

      }

      component InnerInner<String, Integer> innerinner;
  }

  component InnerWithInner<Boolean> innerwithinner;

}