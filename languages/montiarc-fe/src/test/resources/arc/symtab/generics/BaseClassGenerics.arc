package generics;

component BaseClassGenerics extends SubGeneric<String> {
  ports in Boolean boolIn,
        out Integer intOut;
}