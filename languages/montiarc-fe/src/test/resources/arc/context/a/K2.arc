package a;

import java.lang.String;

component K2 {

  port 
    in String strIn,
    out String strOut;
  
  component y.R6GenericPartner<java.lang.String> p;
  
  connect p.tOut -> strOut;
  connect strIn -> p.tIn;
  
  connect strIn -> p.tIn.wrong;
  connect p.tOut.wrong2 -> strOut;
  
  
  
   
}