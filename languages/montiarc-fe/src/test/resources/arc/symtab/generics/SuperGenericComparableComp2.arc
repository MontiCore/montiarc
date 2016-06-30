package generics;

component SuperGenericComparableComp2<K, T extends Comparable<T>> {

    port
        in T tIn,
        out K tOut;
}
