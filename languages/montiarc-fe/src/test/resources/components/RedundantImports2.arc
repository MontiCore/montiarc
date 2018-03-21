package components;

import components.*; // The current package 'components' should not be imported!

import components.DefinedJavaTypeSamePackage; // Duplicate import
  // The model 'a.DefinedJavaTypeSamePackage' is in the current package
  //  so it must not be imported!

import java.lang.String;
import java.lang.String; // The model 'java.lang.String' is imported more then once!

import java.io.*;
import java.io.*; // WARNING: The package 'java.io' is imported more then once!

import components.body.subcomponents._subcomponents.package1
          .SameComponentNameInDifferentPackage;
import components.body.subcomponents._subcomponents.package2
          .SameComponentNameInDifferentPackage;
    //WARNING: A Type with the name 'SameComponentNameInDifferentPackage' is already imported!

/*
 * Invalid model
 *
 * Formerly named "S1" in MontiArc3.
 *
 * @implements [Hab16] CV3: Duplicated imports should be avoided. (p.71, no listing)
 * TODO Add test for missing warnings as well as the missing CoCo(s)
 */
component RedundantImports2 {
  
  port 
    in String,
    in Serializable,
    out DefinedJavaTypeSamePackage;
    
  component SameComponentNameInDifferentPackage s11;
  component components.body.subcomponents._subcomponents
  .package2.SameComponentNameInDifferentPackage s12;
  
  connect string -> s11.string;
  connect serializable -> s11.serializable;
  connect s12.pOut -> definedJavaTypeSamePackage;
}