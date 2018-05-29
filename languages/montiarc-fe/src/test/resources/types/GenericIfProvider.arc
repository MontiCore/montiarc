package types;

/*
 * Valid model.
 */
component GenericIfProvider<T> {
    
    port
        out MyGenericImpl<T> implOut,
        out GenericInterface<T> ifOut;
}