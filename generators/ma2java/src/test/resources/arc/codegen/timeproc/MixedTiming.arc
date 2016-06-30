package timeproc;

component MixedTiming {
    
    port
        in String sIn1,
        in String sIn2,
        out String sOut;
        
    component UntimedComponent utc;
    
    component UntimedSimpleInComponent utsc;
    
    component Sub s1, s2;
    
    connect sIn1 -> s1.sIn1, utc.str2In;
    connect sIn2 -> s1.sIn2, utsc.strIn;
    
    connect s1.sOut -> utc.str1In;
    
    connect utc.strOut -> s2.sIn1;
    connect utsc.strOut -> s2.sIn2;
    
    connect s2.sOut -> sOut;     

}