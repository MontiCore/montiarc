package interfaze;

component GenericIfRequired<T> {
    port
        in MyGenericImpl<T> implIn,
        in MyGenericInterface<T> ifIn;

}