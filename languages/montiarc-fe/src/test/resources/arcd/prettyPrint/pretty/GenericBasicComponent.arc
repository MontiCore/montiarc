package pretty;

component GenericBasicComponent<T> {
  port
    in T portIn,
    in GenericType<T> myPort,
    out T portOut;

}