package simpleDeployment;

component BWithSubcomponentA {
 
 ports
  in Integer bIn,
  out String bOut;
  
  component A a;
  
  connect bIn -> a.aIn;
  connect a.aOut -> bOut;
  
 
}