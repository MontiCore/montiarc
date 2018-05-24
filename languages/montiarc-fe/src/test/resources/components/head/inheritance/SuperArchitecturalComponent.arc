package components.head.inheritance;

import components.body.subcomponents._subcomponents.HasStringInputAndOutput;

/**
 * Valid model.
 * Used as supercomponent. Untested
 */
component SuperArchitecturalComponent {
    
    port 
        in String myInput,
        out String myOutput,
        out String myOutput519;
        
   component HasStringInputAndOutput myA [pOut -> myOutput];
   
   connect myInput -> myA.pIn;

}