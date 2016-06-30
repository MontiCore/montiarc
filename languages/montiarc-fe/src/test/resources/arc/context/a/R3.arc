package a;

import java.lang.String;
import java.lang.Integer;

component R7 {

  port 
    in String str1,
    in String str2,
    out Integer int1,
    out Integer int2;
  
  component x.R5Partner p;
  
  connect str1 -> p.portIn;
  connect p.portOut -> int1;
  
  connect str2 -> undefRef1.portIn;
  connect RefType.portOut -> int2;
  connect undefRef3.somePort -> p.Boolean;
  
  

}