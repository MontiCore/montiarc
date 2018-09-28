package components.body.ajava;

/*
 * Valid model.
 */
component UsedPortsAndParametersExist(String s) {
  port
    in double distance,
    out String hulu;
    
  init EmptyStringInitializer {
    hulu = "";
  } 

  compute { 
    String s = "x";   
    hulu = s;
  }
}