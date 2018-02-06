package components.body.connectors;

/**
 * Valid model.
 * @implements [Hab16] CO2: A simple connector’s source is an outgoing port of the 
 */
component SimpleConnectorSourceFullyQualified {
  component A {
    port out String aOut;
  }
  
  component B {
    port in String bIn;
  }
  
  connect a.aOut -> b.bIn;
  
  component A a1 [a1.aOut -> b.bIn];
}