package generics;

import java.io.Serializable;

component DecomposedGeneric<K, V extends Serializable> {

    port
        in K sIn1,
        in V sIn2,
        out K sOut1,
        out V sOut2;

    component SubGeneric<K> s1;
    component SubGeneric<V> s2;
    
    connect sIn1 -> s1.sIn1, s1.sIn2;
    connect sIn2 -> s2.sIn1, s2.sIn2;
    
    connect s1.sOut -> sOut1;
    connect s2.sOut -> sOut2; 

}