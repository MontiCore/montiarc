package components.body.ports;

import types.TypesWithHierarchy.DefinedType;
import types.package2.*;
import types.package1.DefinedJavaTypeInPackage1;

/*
 * Invalid model.
 *
 * Formerly named "CG7" in MontiArc3.
 */
component UndefinedPortTypes {

  port in DefinedType dt; //Correct
  port in types.package3.DefinedJavaTypeInPackage3 dtj; //Correct
  port in UndefinedType;
    //ERROR: The type 'UndefinedType' could not be loaded!
  port out AnotherUndefinedType aut;
    //ERROR: The type 'AnotherUndefinedType' could not be loaded!
  port out DefinedJavaTypeInPackage2; //Correct
  port out DefinedJavaTypeInPackage1; //Correct
  port out types.UndefinedTypeFQ utfq;
    //ERROR: The type 'types.UndefinedTypeFQ' could not be loaded!

    component Inner {
      port in DefinedType dt; //Correct
      port in types.package3.DefinedJavaTypeInPackage3 dtj; //Correct
      port in UndefinedType;
        //ERROR: The type 'UndefinedType' could not be loaded!
      port out AnotherUndefinedType aut;
        //ERROR: The type 'AnotherUndefinedType' could not be loaded!
      port out DefinedJavaTypeInPackage2; //Correct
      port out DefinedJavaTypeInPackage1; //Correct
      port out types.UndefinedTypeFQ utfq;
        //ERROR: The type 'types.UndefinedTypeFQ' could not be loaded!
    }

    connect dt -> inner.dt;
    connect dtj -> inner.dtj;
    connect undefinedType -> inner.undefinedType;
    connect inner.aut -> aut;
    connect inner.definedJavaTypeInPackage2 -> definedJavaTypeInPackage2;
    connect inner.definedJavaTypeInPackage1 -> definedJavaTypeInPackage1;
    connect inner.utfq -> utfq;
}