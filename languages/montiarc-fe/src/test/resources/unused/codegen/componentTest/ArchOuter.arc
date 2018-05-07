package componentTest;

import java.lang.String;
import java.lang.Integer;

component ArchOuter {
  
  port
    in String str1,
    in String str2,
    out Integer int1,
    out Integer int2,
    out Integer int3;
    
  component Simple;
  component SimpleOut;
  component ArchInner;
  component SimpleOutGeneric<Integer>;
  
  connect str1 -> simpleOut.str1, simple.string, simpleOutGeneric.string;
  connect str2 -> archInner.string, simpleOut.str2;
  connect simple.integer -> simpleOutGeneric.input1;
  connect simpleOut.integer -> simpleOutGeneric.input2;
  connect simpleOutGeneric.output -> int3;
  connect archInner.intOut1 -> int1;
  connect archInner.intOut2 -> int2;
  
}