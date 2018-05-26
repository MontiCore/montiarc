package components.body.subcomponents;

import components.body.subcomponents._subcomponents.*;

/**
* Invalid model.
*
* @implements [Hab16] R9: If a generic component type is instantiated 
* as a subcomponent, all generic parameters have to be assigned. (p. 66, lst. 3.44)
* @implements [Hab16] R10: If a configurable component is instantiated as a 
* subcomponent, all configuration parameters have to be assigned. (p. 67, lst. 3.45)
* TODO Add test
*/
component SubcomponentsWithCfgArgsAndTypeParams(int myInt, String myString) {
    port 
        in String s1;  
    
    component GenericConfigurableComponent gccWrong1; //wrong: both type params and cfg args not set
    
    component GenericConfigurableComponent<T> gccWrong2; //wrong: 1/2 type params set 
                                                         //and both cfg args missing
   
    component GenericConfigurableComponent(1, "Hallo") gccWrong4; //wrong: both type params missing
    
    component GenericConfigurableComponent<T, String>(1) gccWrong5; //wrong: only 1/2 cfg args set
    
    component GenericConfigurableComponent<T>(123, "Test") gccWrong3; //wrong: only 1/2 type params set
    
    component GenericConfigurableComponent<T, String>(1, "Hallo") gccCorrect1; //correct
    
    component GenericConfigurableComponent<Integer, String>(myInt, myString) gccCorrect2; //correct
    
  
    connect s1 -> gccWrong1.s1;
    connect s1 -> gccWrong2.s1; 
    connect s1 -> gccWrong3.s1;
    connect s1 -> gccWrong4.s1;
    connect s1 -> gccWrong5.s1;
    connect s1 -> gccCorrect1.s1;
    connect s1 -> gccCorrect2.s1;

}