package a;

import x.R2RefQualifiedImported;
import q.*;
import q.z.a.*;
import myTypes.*;

component CG10 {
  port in DefinedType;
  component R2RefQualifiedImported ref;
  
  connect definedType -> ref.definedType;
}