/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

/**
 * Valid model.
 */
component SimpleConnectorSourceExists {
  component A a{
    port out String aOut;
  }

  component B {
    port in String bIn1,
         in String bIn2;
  }

  connect a.aOut -> b.bIn1;

  component A myA [aOut -> b.bIn2];
}
