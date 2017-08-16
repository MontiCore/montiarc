package a;

component GenericCompWithInnerGenericComp<T> {

    port
        in T input;
        
    component InnerGeneric<T> {
        port
            in T input;
    }
    
    component InnerGeneric<T> inner;
    
    connect input -> inner.input;

}