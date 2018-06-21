package components.body.ports;

/*
 * Invalid model.
 *
 * Formerly named "PortCompatibility2"
 *
 * @implements [Hab16] R8: The target port in a connection has to be compatible
 *                           to the source port, i.e., the type of the target
 *                           port is identical or a supertype of the source
 *                           port type. (p.66, Lst. 3.43)
 */
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
    connect input -> myInner1.kInput; // OK
    connect myInner1.kOutput -> output; // OK
    
    component Inner<String> myInner2;
    connect input -> myInner2.kInput; // ERROR
    connect myInner2.kOutput -> output2; // ERROR
    
    component Inner<V> myInner3;
    connect input -> myInner3.kInput; //ERROR
    connect myInner3.kOutput -> output3; //ERROR

}