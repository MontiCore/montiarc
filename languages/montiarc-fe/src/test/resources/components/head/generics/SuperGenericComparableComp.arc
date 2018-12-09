package components.head.generics;

/*
 * Invalid model.
 * Generics with type bounds are currently prohibited. See #241
 */
component SuperGenericComparableComp<T extends Comparable<T>> {

    port
        in T tIn,
        out T tOut;
}
