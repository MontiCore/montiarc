package components.body.connectors;

/**
 * Valid model.
 */
component SimpleConnectorSourceExistsExternal {
  component B {
    port in String bIn;
  }
  
  component StringSender a [sOut -> b.bIn];
}