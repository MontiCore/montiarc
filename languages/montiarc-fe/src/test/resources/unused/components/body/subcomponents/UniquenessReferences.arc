package components.body.subcomponents;
import components._subcomponents.package2.ValidComponentInPackage2;
import components._subcomponents.AtomicComponent;

/*
 * Invalid model.
 * Produces 2 errors and 1 warning in MontiArc3.
 * TODO: Correct amounts? Might be 2 warnings, 1 error
 * Warning: "ArcdErrorCodes.NamingConventionsInjured"
 *
 * @implements [Hab16] CV7: Avoid using implicit and explicit names for
 *                            elements with the same type. (p. 72, Lst. 3.54)
 * @implements [Hab16] B1: All names of model elements within a component
                              namespace have to be unique (p.59)
 *
 * TODO: Add Test
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

    component ValidComponentInPackage2 anotherInB;
    
    component ValidComponentInPackage2 validComponentInPackage2;
       // Error: Has the same component name as the unnamed instance
    
    component ValidComponentInPackage1;
       // Warning: Implicit naming should be used for unique subcomponent types only
    
    component ValidComponentInPackage1 anotherInA;

    component AtomicComponent atomic;

    component AtomicComponent atomic2;


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