/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

import components.body.subcomponents._subcomponents.package1.ValidComponentInPackage1;
import components.body.subcomponents._subcomponents.package2.ValidComponentInPackage2;
import components.body.subcomponents._subcomponents.AtomicComponent;

/*
 * Invalid model.
 *
 * @implements [Hab16] CV7: Avoid using implicit and explicit names for
 *                            elements with the same type. (p. 72, Lst. 3.54)
 * @implements [Hab16] B1: All names of model elements within a component
                              namespace have to be unique (p.59)
 *
 * TODO: Adjust test for CV7
 * TODO CV7 CoCo and Test
 */
component UniquenessReferences {
    port
        in String s1,
        out String sOut1,
        out String sOut2,
        out String sOut3,
        out String sOut4,
        out String sOut5;

    component ValidComponentInPackage2;
       // Warning: Implicit naming should be used for unique subcomponent types only
       // Error: Has the same component name as the unnamed instance

    component ValidComponentInPackage2 anotherInB;

    component ValidComponentInPackage2 validComponentInPackage2;
       // Error: Has the same component name as the unnamed instance

    component ValidComponentInPackage1;
       // Warning: Implicit naming should be used for unique subcomponent types only

    component ValidComponentInPackage1 anotherInA;

    component AtomicComponent atomic;

    component AtomicComponent atomic2;
      // Error: Ambiguous component name

    component AtomicComponent atomic2;
      // Error: Ambiguous component name


    connect s1 -> anotherInB.stringIn,
                  validComponentInPackage2.stringIn,
                  anotherInA.stringIn,
                  correctCompInA.stringIn;

    connect validComponentInPackage2.stringOut -> sOut1;

    connect anotherInB.stringOut -> sOut2;

    connect validComponentInPackage2.stringOut -> sOut3;

    connect correctCompInA.stringOut -> sOut4;

    connect anotherInA.stringOut -> sOut5;

}
