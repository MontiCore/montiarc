package a;

import types.TypesWithHierarchy.DefinedType;
import types.package2.*;
import types.package1.DefinedJavaTypeInPackage1;

/*
 * Invalid model
 *
 * Formerly named "CG7" in MontiArc3.
 *
 * @implements TODO
 * TODO Add test
 * TODO Package
 */
component UndefinedPortTypes {

  port
    in DefinedType dt, //Correct

    in types.package3.DefinedJavaTypeInPackage3 dtj, //Correct

    in UndefinedType,
        //ERROR: The type 'UndefinedType' could not be loaded!

    out AnotherUndefinedType aut,
        //ERROR: The type 'AnotherUndefinedType' could not be loaded!

    out DefinedJavaTypeInPackage2, //Correct

    out DefinedJavaTypeInPackage1, //Correct

    out types.UndefinedTypeFQ utfq;
        //ERROR: The type 'types.UndefinedType' could not be loaded!
}