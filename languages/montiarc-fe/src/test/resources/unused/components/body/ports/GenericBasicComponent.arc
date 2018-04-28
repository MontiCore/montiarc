package unused.components.body.ports;

component GenericBasicComponent<T> {
  port
    in T portIn,
    in GenericType<T> myPort,
    out T portOut;

}