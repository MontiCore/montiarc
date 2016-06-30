package a;

import b.*;

component ReferenceConnectorCompletion {
    port
        in String strIn,
        out Integer intOut,
        out String strOut,
        out Integer intOut2,
        out Integer intOut3;
        
    component SimpleComp simpleOne, simpleTwo;
    
    //should result in strIn -> simpleOne.stringIn
    connect strIn -> simpleOne;
    
    // should result in simpleOne.intergerOut -> intOut and simpleOne.stringOut -> strOut    
    connect simpleOne -> intOut, strOut;
    
    // should result in simpleTwo.integerOut -> intOut2, intOut3
    connect simpleTwo -> intOut2, intOut3;
    
}