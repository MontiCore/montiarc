package components.body.connectors;

/**
 * Valid model.
 * @implements [Hab16] CO1: Connectors may not pierce through component interfaces. (p. 60, Lst. 3.33)
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