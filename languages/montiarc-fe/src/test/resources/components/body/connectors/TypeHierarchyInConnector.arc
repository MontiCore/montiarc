package components.body.connectors;

/**
 * Invalid model.
 * See https://git.rwth-aachen.de/monticore/montiarc/core/issues/243
 *
 */
component TypeHierarchyInConnector {

  component A {
    port in Collection<String> inColl;
  }
  
  port in List<String> inList;
  
  component A subComp;
  
  connect inList -> subComp.inColl; //List extends Collection
    // ERROR, due to #243
  

}