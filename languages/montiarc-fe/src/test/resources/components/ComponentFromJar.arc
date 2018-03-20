package components;

import a.b.ComponentInJar;

component ComponentFromJar {

  port
    in String sIn,
    out String sOut;
    
  component ComponentInJar cmp;
  
  connect sIn -> cmp.sIn;
  connect cmp.sOut -> sOut;
  

}