package setup;

component WithGenerics<V extends Comparable<V>, K> {
  
  port 
    in java.util.List<K> kIn,
    out V vOut;
}