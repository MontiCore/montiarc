package a;

import b.*;
import c.CComp;
import java.io.*;
import java.lang.System;
<<stereo1 = "someValue", stereo2>> component Sub1 extends SuperSamePackage {

  port  
    in String stringIn, 
    <<disabled="held", initialOutput = "1", ignoreWarning>> in Integer integerIn,
    <<disabled="reset", initialOutput = "0">> out String stringOut,
    <<portStereo1>> out Number,
    <<portStereo2>> out Integer integerOut,
    out System,
    out PrintStream ps;
  
  component b.SuperSamePackage super1;
  component Sub2;
  <<refStereo>> component SuperOtherPackage super2;
  component CComp;
  component SuperOtherPackage super3, super4;
  
  
  <<conStereo>> connect Sub2.String -> stringOut;
  connect sub2.Integer -> integerOut;
  connect stringIn -> super1.input, super2.input;
  connect sub2.String -> super3.input;
}