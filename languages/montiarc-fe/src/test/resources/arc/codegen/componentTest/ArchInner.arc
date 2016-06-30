package componentTest;

import java.lang.String;
import java.lang.Integer;

component ArchInner {
  port 
   in String,
   out Integer intOut1,
   out Integer intOut2;
 
  component SimpleIn;
  component Complex;
  component Merge<Integer>("ISO-8859-1");
  
  connect string -> simpleIn.string, complex.str1, complex.str2;
  connect simpleIn.int1 -> merge.input1;
  connect simpleIn.int2 -> merge.input2;
  connect complex.int1 -> merge.input3;
  connect complex.int2 -> merge.input4;
  
  connect merge.output1 -> intOut1;
  connect merge.output2 -> intOut2;
  

}