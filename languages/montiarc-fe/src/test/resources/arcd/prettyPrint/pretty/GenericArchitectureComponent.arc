package pretty;

component GenericArchitectureComponent<T> {
  
  port 
    in T myIncomingPort,
    out T myOutgoingPort;
    
  component GenericBasicComponent<T> g;
  
  connect myIncomingPort -> g;
  connect g -> myOutgoingPort;

}