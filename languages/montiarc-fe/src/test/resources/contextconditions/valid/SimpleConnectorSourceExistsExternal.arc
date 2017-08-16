package valid;

component SimpleConnectorSourceExistsExternal {
  component B {
    port in String bIn;
  }
  
  component StringSender a [sOut -> b.bIn];
}