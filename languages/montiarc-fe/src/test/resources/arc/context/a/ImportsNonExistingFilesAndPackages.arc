package a;

import components.body.subcomponents._subcomponents.HasPortWithImportedType;
import q.*; //ERROR: The package 'q' does not exist in the filesystem or
            //        as a class diagram!
            //WARNING: The imported package 'q' is never used!
import q.z.a.*; //ERROR: The package 'q.z.a' does not exist in the filesystem
                //        or as a class diagram!
                //WARNING: The imported package 'q.z.a' is never used!
import types.TypesWithHierarchy.*;

/*
 * Invalid model
 *
 * Formerly named "CG10" in MontiArc3.
 *
 * @implements [Hab16] CV4: Unused direct imports should be avoided.
 *                          (p. 71)
 * @implements TODO: Literature for non-existing imports
 * TODO Add test
 */
component ImportsNonExistingFilesAndPackages {
  port in DefinedType;
  component HasPortWithImportedType ref;
  
  connect definedType -> ref.definedType;
}