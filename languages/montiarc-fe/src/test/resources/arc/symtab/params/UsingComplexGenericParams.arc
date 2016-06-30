package params;

import java.util.*;

component UsingComplexGenericParams<K, V> {

    component ComplexGenericParams<K, V> (new int[]{1, 2, 3}, new HashMap<List<K>, List<V>>()) cp;
     
}
