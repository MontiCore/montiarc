package a;

import d.*;
import b.GenericComponent;

component PortCompatibility {
    
    port 
        in Integer intIn,
        out Integer intOut1,
        out Integer intOut2;
    
    component CorrectCompInA ccia [stringOut -> intOut1];
    
    component CorrectCompInA ccia2;
    
    connect intIn -> ccia.stringIn, ccia2.stringIn;
    
    connect ccia2.stringOut -> intOut2;
    
    
    port
        in SuperType supIn,
        in SubType subIn,
        out SuperType supOut1,
        out SubType subOut1,
        out SuperType supOut2,
        out SubType subOut2;
    
    component CompWithJavaTypes 
        cwjt [supOut -> subOut1, supOut1;
               subOut -> subOut2, supOut2];
    
    connect supIn -> cwjt.supIn1;
    connect subIn -> cwjt.supIn2, cwjt.subIn2;
    
    connect supIn -> cwjt.subIn1;
    
    
    port
    	in String strIn1,
    	in Boolean boolIn1,
    	out Integer intOut3,
    	out Boolean boolOut1;
    	
    component GenericComponent<String, Integer> myGenComp;
    
    connect strIn1 -> myGenComp.myKIn;
    connect myGenComp.myVOut -> intOut3;
    
        component GenericComponent<String, Integer> myGenComp2;
    
    connect boolIn1 -> myGenComp2.myKIn;
    connect myGenComp2.myVOut -> boolOut1;
    
    
}