package a;

import a.*;

import a.DefinedJavaTypeSamePackage;

import java.lang.String;
import java.lang.String;

import java.io.*;
import java.io.*;

import x.S1Double;
import y.S1Double;

component S1 {
  
  port 
    in String,
    in Serializable,
    out DefinedJavaTypeSamePackage;
    
  component S1Double s11;
  component y.S1Double s12;
  
  connect string -> s11.string;
  connect serializable -> s11.serializable;
  connect s12.pOut -> definedJavaTypeSamePackage;
}