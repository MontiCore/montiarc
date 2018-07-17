package components.body.subcomponents;

import components.body.subcomponents._subcomponents.*;

/**
* Invalid model.
*
* @implements [Hab16] R9: If a generic component type is instantiated 
* as a subcomponent, all generic parameters have to be assigned. (p. 66, lst. )
*/
component SubcomponentsWithWrongNumberOfCfgArgs(Integer myInt, String myString) {
    port 
        in String s1;
        
    component ConfigurableComponent ccWrong1; //config args have to be set.
    
    component ConfigurableComponent(1) ccWrong2; // only one of two config args set
    
    component ConfigurableComponent(1, "Hallo") ccCorrect1; // correct
    
    component ConfigurableComponent(myInt, myString) ccCorrect2; // correct
    
    
    connect s1 -> ccWrong1.s1; 
    connect s1 -> ccWrong2.s1; 
    connect s1 -> ccCorrect1.s1; 
    connect s1 -> ccCorrect2.s1; 

}