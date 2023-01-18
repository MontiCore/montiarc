/* (c) https://github.com/MontiCore/monticore */
package components.head.generics;

/*
 * Valid model.
 */
component SuperGenericComparableComp<T extends Comparable<T>> {

    port
        in T tIn,
        out T tOut;
}
