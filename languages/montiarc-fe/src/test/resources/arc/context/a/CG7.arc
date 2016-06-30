package a;

import myTypes.DefinedType;
import y.*;
import x.DefinedJavaTypeInX;

component CG7 {

  port 
    in DefinedType dt,
    in z.DefinedJavaTypeInZ dtj,
    in UndefinedType,
    out AnotherUndefinedType aut,
    out DefinedJavaTypeInY,
    out DefinedJavaTypeInX,
    out x.UndefinedTypeFQ utfq;
}