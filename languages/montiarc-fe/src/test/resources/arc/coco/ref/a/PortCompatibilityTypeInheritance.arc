package a;

import d.*;

component PortCompatibilityTypeInheritance {
    
    port
        in SuperType supIn,
        in SubType subIn,
        out SuperType supOut1,
        out SubType subOut1,
        out SuperType supOut2,
        out SubType subOut2;

    component CompWithJavaTypes 
        cwjt [supOut -> subOut1, supOut1;  // incompatible (SuperType -> SubType)
               subOut -> subOut2, supOut2];
    
    connect supIn -> cwjt.supIn1; // compatible 
    connect subIn -> cwjt.supIn2, cwjt.subIn2; // compatible
    
    connect supIn -> cwjt.subIn1; // incompatible
   
}