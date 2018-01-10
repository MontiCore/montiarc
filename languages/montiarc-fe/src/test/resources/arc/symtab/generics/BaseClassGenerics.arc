package generics;

component BaseClassGenerics extends SubGeneric<String> {
  port in Boolean boolIn,
        out Integer intOut;
}