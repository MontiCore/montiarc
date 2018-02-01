package components.body.autoconnect;

/**
 * Invalid model. Duplicate matches for String and Booleans.
 */
component DuplicateAutoconnectMatches {
    autoconnect type;
    
    port 
        in String strIn,
        in String strIn2,
        in Integer intIn,
        out Boolean boolOut1,
        out Boolean boolOut2,
        out Double doubleOut;
    
    component Inner {
        port 
            in String myString,
            in Integer myInteger,
            out Boolean myBoolean,
            out Double myDouble;
    }
    
    /**
    expected connectors:
    intIn -> Inner.myInteger;
    Inner.myBoolean -> boolOut1;
    Inner.myBoolean -> boolOut2;
    Inner.myDouble -> doubleOut; 
    */
    
}