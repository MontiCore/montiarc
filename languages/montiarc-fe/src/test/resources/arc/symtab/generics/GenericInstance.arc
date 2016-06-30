package generics;

component GenericInstance {
  component Generic<T extends Number> {
    ports in T in1,
          in T in2,
          out T out1;

    component SuperGenericComparableComp2<String, T> sc1;
    component SuperGenericComparableComp2<Integer, T> sc2;
  }

  component Generic<Double> gDouble;
  component Generic<Integer> gInteger;
}