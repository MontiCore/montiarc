package a;

import types.TypesWithHierarchy.DefinedType;
import y.*;
import cg13.x.DefinedJavaTypeInX;

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