package component.body.ajava;

component UsedPortsAndParametersExist(String s) {
  port
    in double distance,
    out String hulu;
    
  init EmptyStringInitializer {
    hulu = "";
  } 

  compute {    
    hulu = s;
  }
}