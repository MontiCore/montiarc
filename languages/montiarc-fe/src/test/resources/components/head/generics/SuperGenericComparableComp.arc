package components.head.generics;

/*
 * Valid model.
 * TODO Add test
 */
component SuperGenericComparableComp<T extends Comparable<T>> {

    port
        in T tIn,
        out T tOut;
}
