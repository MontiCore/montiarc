/* (c) https://github.com/MontiCore/monticore */
package components.head.generics;

/*
 * Invalid model.
 * Generic type parameters with type bounds are currently prohibited. See #241
 */
component SuperGenericComparableComp2<K, T extends Comparable<T>> {

    port
        in T tIn,
        out K tOut;
}
