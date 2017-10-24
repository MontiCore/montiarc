package conv;

component UnusedPorts {

    port
        in Integer usedInputInteger,
        in String usedInputString,
        in Boolean unusedInputBoolean, //not connected
        out Integer usedOutputInteger,
        out Short unusedOutputShort; //not connected
        
    component InnerComp {
        port
            in Integer innerInputInteger,
            in String innerInputString,
            in Integer unusedInnerInputInteger, //not connected outside, only in InnerComp
            out Integer innerOutputInteger, //not connected in InnerComp, only outside
            out String unusedInnerOutputString; //not connected outside, only in InnerComp 
            

        component InnerInnerComp {
            port
            	in String innerInputString2,
                in Integer innerInputInteger,
                in String innerInputString,
                out String innerOutputString;
        }
        
        component SimpleComp innerSimple [usedOutputInteger -> innerInnerComp.innerInputInteger]; //has one unconnected port
        connect unusedInnerInputInteger -> innerInnerComp.innerInputString2;
        connect innerInnerComp.innerOutputString -> unusedInnerOutputString;
        connect innerInputInteger -> innerSimple.usedInputInteger;
        connect innerInputString ->  innerInnerComp.innerInputString;
    }
    
    component SimpleComp innerRef [usedOutputInteger -> innerComp.innerInputInteger]; //has one unconnected port

    connect usedInputInteger -> innerRef.usedInputInteger;
    connect usedInputString -> innerComp.innerInputString;
    connect innerComp.innerOutputInteger -> usedOutputInteger;
}