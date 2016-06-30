package a;

import b.*;

component CorrectReferences<T>(int myInt, String myString) {
    port 
        in String s1;
        
    component SimpleGenericComponent gcWrong1;
    
    // need two type parameters
    component SimpleGenericComponent<T> gcWrong2;
    
    component SimpleGenericComponent<T, String> gcCorrect1;
    
    component SimpleGenericComponent<Integer, String> gcCorrect2;
    
    component ConfigurableComponent ccWrong1;
    
    component ConfigurableComponent(1) ccWrong2;
    
    component ConfigurableComponent(1, "Hallo") ccCorrect1;
    
    component ConfigurableComponent(myInt, myString) ccCorrect2;
    
    
    
    component GenericConfigurableComponent gccWrong1;
    
    component GenericConfigurableComponent<T> gccWrong2;
   
    component GenericConfigurableComponent(1, "Hallo") gccWrong4;
    
    component GenericConfigurableComponent<T, String>(1) gccWrong5;
    
    component GenericConfigurableComponent<T>(123, "Test") gccWrong3;
    
    component GenericConfigurableComponent<T, String>(1, "Hallo") gccCorrect1;
    
    component GenericConfigurableComponent<Integer, String>(myInt, myString) gccCorrect2;
    
    connect s1 -> gcWrong1.stringIn; 
    connect s1 -> gcWrong2.stringIn;
    connect s1 -> gcCorrect1.stringIn;
    connect s1 -> gcCorrect2.stringIn; 
    connect s1 -> ccWrong1.s1; 
    connect s1 -> ccWrong2.s1; 
    connect s1 -> ccCorrect1.s1; 
    connect s1 -> ccCorrect2.s1; 
    connect s1 -> gccWrong1.s1;
    connect s1 -> gccWrong2.s1; 
    connect s1 -> gccWrong3.s1;
    connect s1 -> gccWrong4.s1;
    connect s1 -> gccWrong5.s1;
    connect s1 -> gccCorrect1.s1;
    connect s1 -> gccCorrect2.s1;

}