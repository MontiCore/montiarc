package a;

import java.lang.String;
import java.lang.Integer;
import y.R6GenericPartner;
component R8 {
  port
    in String in1,
    in String in2, 
    out Integer out1;
    
  component R6GenericPartner<String> p1;
  component R6GenericPartner<Integer> p2;
  component R6GenericPartner<Integer> p3;
  component R6GenericPartner<Integer> p4;
  
  // correct
  connect in1 -> p1.tIn;
  connect p2.tOut -> out1;
  connect p2.tOut -> p4.tIn;
  
  // wrong, added a sender twice or more
  connect in2 -> p1.tIn;
  connect p3.tOut -> out1;
  connect p4.tOut -> out1;
  connect p3.tOut -> p4.tIn;
}