package a;

import y.R6GenericPartner;
import ma.sim.FixDelay;
import java.lang.String;

component CG2 {

  port 
    in String strIn,
    out String strOut; 

  component x.CG2Partner<String> p1;
  component R6GenericPartner<String> p2;
  component R6GenericPartner<String> p3;
  component R6GenericPartner<String> p4;
  component R6GenericPartner<String> p5;
  component R6GenericPartner<String> p6;
  component FixDelay<String>(1) delay;
  
  connect p1.tOut -> p2.tIn, p3.tIn;
  connect p2.tOut -> p1.tIn1, p4.tIn;
  connect p3.tOut -> p5.tIn;
  connect p5.tOut -> p6.tIn;
  connect p6.tOut -> p1.tIn2;
  
  // correct loop
  connect p4.tOut -> delay.portIn;
  connect delay.portOut -> p1.tIn3;
  
  // self loop
  connect strIn -> strOut;
  
    

}