/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

/**
 * Invalid model.
 TODO: See test ignore
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
