package types;

component GenericIfProvider<T> {
    
    port
        out MyGenericImpl<T> implOut,
        out MyGenericInterface<T> ifOut;

}