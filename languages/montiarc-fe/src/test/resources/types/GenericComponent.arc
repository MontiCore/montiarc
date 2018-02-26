package types;

import java.util.*;

component GenericComponent<K, V> {
	
	port 
		in K myKIn1,
		in K myKIn2,
		in List<K> myListKIn1,
		in List<K> myListKIn2,
		in List<Map<K,V>> myListMapKVIn1,
		in List<Map<K,V>> myListMapKVIn2,
		in List<Map<K,K>> myListMapKKIn1,
		in List<Map<K,K>> myListMapKKIn2,
		out V myVOut,
		out List<K> myListKOut,
        out List<Map<K,V>> myListMapKVOut;
}