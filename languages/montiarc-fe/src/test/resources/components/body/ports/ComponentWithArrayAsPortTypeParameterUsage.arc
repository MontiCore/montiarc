package components.body.ports;

import types.CType;

/**
* Valid model.
*/
component ComponentWithArrayAsPortTypeParameterUsage {
    
    port
        in GenericType<byte[]> genIn,
        in GenericType<CType[]> getCTypeIn,
        out GenericType<byte[]> genOut,
        out GenericType<CType[]> getCTypeOut;
        
    component ComponentWithArrayAsPortTypeParameter a1, a2;
    
    connect genIn -> a1.genIn;
    connect getCTypeIn -> a1.getCTypeIn;
    
    connect a1.genOut -> a2.genIn;
    connect a1.getCTypeOut -> a2.getCTypeIn;
    
    connect a2.genOut -> genOut;
    connect a2.getCTypeOut -> getCTypeOut;

}