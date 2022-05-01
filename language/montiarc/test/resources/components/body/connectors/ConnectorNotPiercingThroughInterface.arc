/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

/**
 * Valid model.
 */
component ConnectorNotPiercingThroughInterface {
  component A {
    port in String aIn;
    
    component B {
      port in String bIn;
    }
    
    connect aIn -> b.bIn;
  }
  
  component C {
    port out String cOut;
  }
  
  connect c.cOut -> a.aIn;
}
