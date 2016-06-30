package ocl;

component OCLFieldToPort {
  port in String switchStatus; 
  
  component Arbiter a {
    port in String switchStatus;
  }
    
  ocl inv myInv:
    forall sws in switchStatus:
      sws isin a.switchStatus;
  
  connect switchStatus -> a.switchStatus;
}
