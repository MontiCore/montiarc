package invalid;

component SimpleConnectorSourceNonExistant {
  component A {
  }
  
  component B {
    port in String bIn;
  }
  
  component A myA [aOut -> b.bIn];
}