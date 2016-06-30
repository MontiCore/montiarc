package pretty;

import b.*;

component CorrectReferences<T>(int myInt, String myString) {
    
    component GenericComponent gcWrong1;
    
    // need two type parameters
    component GenericComponent<T> gcWrong2;
    
    component GenericComponent<T, String> gcCorrect1;
    
    component GenericComponent<Integer, String> gcCorrect2;
    
    component ConfigurableComponent ccWrong1;
    
    component ConfigurableComponent(1) ccWrong2;
    
    component ConfigurableComponent(1, "Hallo") ccCorrect1;
    
    component ConfigurableComponent(myInt, myString) ccCorrect2;
    
    
    
    component GenericConfigurableComponent gccWrong1;
    
    component GenericConfigurableComponent<T> gccWrong2;
   
    component GenericConfigurableComponent(1, "Hallo") gccWrong4;
    
    component GenericConfigurableComponent<T, String>(1) gccWrong5;
    
    component GenericConfigurableComponent<T>(123, "Test") gccWrong3;
    
    component GenericConfigurableComponent<T, String>(1, "Hallo") gccCorrect;

}