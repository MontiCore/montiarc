/* (c) https://github.com/MontiCore/monticore */
package components.head.generics;

/**
 * Invalid model.
 * Generic type parameters may not be reused in inner components
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
