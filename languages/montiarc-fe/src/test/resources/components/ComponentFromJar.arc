package components;

import a.b.ComponentInJar;

/*
 * Valid model.
 *
 * Used to assert that it is possible to load components from jars.
 */
component ComponentFromJar {

  port
    in String sIn,
    out String sOut;
    
  component ComponentInJar cmp;
  
  connect sIn -> cmp.sIn;
  connect cmp.sOut -> sOut;
  

}