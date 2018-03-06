package components.body.ports;

component PortCompatibilityWithGenerics2<K, V> {
    port 
        in K input,
        out K output,
        out K output2,
        out K output3;
    
    component Inner<T> {
        port 
            in T kInput,
            out T kOutput; 
    }
    
    component Inner<K> myInner1;
    connect input -> myInner1.kInput;
    connect myInner1.kOutput -> output;
    
    component Inner<String> myInner2;
    connect input -> myInner2.kInput;
    connect myInner2.kOutput -> output2;
    
    component Inner<V> myInner3;
    connect input -> myInner3.kInput;
    connect myInner3.kOutput -> output3;

}