package components.head.generics;

/*
 * Valid model.
 */
component SuperGenericComp<T, K extends Number> {

    port
        in T tIn,
        out K kOut;
}
