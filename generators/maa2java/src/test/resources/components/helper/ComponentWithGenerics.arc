package components.helper;

/*
  Valid component!

  @author Michael Mutert
*/
component ComponentWithGenerics<T,K> {
  port in T inT;
  port out K outK;
}