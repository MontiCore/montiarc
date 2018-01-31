package component.body.ports;

/**
 * Invalid model. Various unconnected ports.
 */
component UnconnectedPorts {

    port
        in Integer usedInputInteger,
        in String usedInputString,
        in Boolean unusedInputBoolean,  // Error: not connected
        out Integer usedOutputInteger,
        out Short unusedOutputShort;    // Error: not connected
        
    component InnerComp {
        port
            in Integer innerInputInteger,
            in String innerInputString,
            in Integer unusedInnerInputInteger, // Error: not connected outside, only in InnerComp
            out Integer innerOutputInteger,     // Error: not connected in InnerComp, only outside
            out String unusedInnerOutputString; // Error: not connected outside, only in InnerComp 
            

        component InnerInnerComp {
            port
            	in String innerInputString2,
                in Integer innerInputInteger,
                in String innerInputString,
                out String innerOutputString;
        }
        
        component SimpleComp innerSimple [usedOutputInteger -> innerInnerComp.innerInputInteger]; // Error: has one unconnected port
        connect unusedInnerInputInteger -> innerInnerComp.innerInputString2;
        connect innerInnerComp.innerOutputString -> unusedInnerOutputString;
        connect innerInputInteger -> innerSimple.usedInputInteger;
        connect innerInputString ->  innerInnerComp.innerInputString;
    }
    
    component SimpleComp innerRef [usedOutputInteger -> innerComp.innerInputInteger]; // Error: has one unconnected port

    connect usedInputInteger -> innerRef.usedInputInteger;
    connect usedInputString -> innerComp.innerInputString;
    connect innerComp.innerOutputInteger -> usedOutputInteger;
}