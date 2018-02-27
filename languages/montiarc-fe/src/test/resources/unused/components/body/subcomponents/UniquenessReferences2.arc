package components.body.subcomponents;
import b.ValidComponentInPackage2; // TODO: Correct import

/*
 * Invalid model.
 * Produces four errors in MontiArc3.
 *
 * @implements [Hab16] B1: All names of model elements within a component
 *    namespace have to be unique. (p. 59. Lst. 3.31)
 *
 * TODO: Add Test
 */
component UniquenessReferences2 {
    port 
        in String s1,
        out String sOut;
        
    component ValidComponentInPackage2; // No explicit instance name
    
    component ValidComponentInPackage2; // No explicit instance name
    
    component ValidComponentInPackage2 ccib;
    
    component ValidComponentInPackage1 anotherInA; // Ambiguous instance name
    
    component ValidComponentInPackage1 anotherInA; // Ambiguous instance name
    
    connect s1 -> ccib.stringIn;
    connect ccib.stringOut -> sOut;
}