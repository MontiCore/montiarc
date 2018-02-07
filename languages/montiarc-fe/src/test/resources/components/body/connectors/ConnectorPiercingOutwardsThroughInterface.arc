package components.body.connectors;

/**
 * Invalid model. Connector pierces through A.
 * @implements [Hab16] CO1: Connectors may not pierce through component interfaces. (p. 60, Lst. 3.33)
 */
component ConnectorPiercingOutwardsThroughInterface {
  component A {
    component Inner {
      port out String innerOut;
    }
  }
  
  component B {
    port in String bIn;
  }
  
  connect a.inner.innerOut -> b.bIn; // Error
}