/* (c) https://github.com/MontiCore/monticore */
package components.head.generics;

/*
 * Valid model.
 */
component SuperGenericComparableComp2<K, T extends Comparable<T>> {

    port
        in T tIn,
        out K tOut;
}
