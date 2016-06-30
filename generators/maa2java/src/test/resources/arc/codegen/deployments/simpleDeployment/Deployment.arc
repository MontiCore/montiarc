package simpleDeployment;

<<deploy>> component Deployment {

  component ValueGenerator generator;
  component ComplexComponent cComponent;
  
  connect generator.value -> cComponent.CCIn;
  
}