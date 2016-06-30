package a;

import java.lang.String;
import y.R6GenericPartner;

component R7 {
  
  port 
    in String usedIn,
    in String unUsedIn, 
    out String usedOut,
    out String unUsedOut;
  
  component R6GenericPartner<String> p1, p2;
  
  connect usedIn -> p1.tIn;
  connect p2.tOut -> usedOut;
}