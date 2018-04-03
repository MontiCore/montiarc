package components.head.generics;

component SuperGenericComp<T, K extends Number> {

    port
        in T tIn,
        out K kOut;
}
