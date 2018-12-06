package components.body.connectors;

component TypeHierarchyInConnector {

  component A {
    port in Collection<String> inColl;
  }
  
  port in List<String> inList;
  
  component A subComp;
  
  connect inList -> subComp.inColl; //List extends Collection
  

}