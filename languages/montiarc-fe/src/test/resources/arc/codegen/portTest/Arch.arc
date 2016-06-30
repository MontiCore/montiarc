package portTest;

component Arch {
  
  port 
    in String str,
    in Integer,
    out Boolean bool;
    
  component Basic b1;
  component Basic2 b2;
  
  connect str -> b1.string, b2.string;
  connect integer -> b2.integer;
  connect b1.bool -> bool, b2.bool;

}