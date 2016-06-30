package pretty;

component Connectors {
    
    connect a -> b;
    connect a -> b1, b2;
    
    connect a -> b.b;
    connect a -> b1.b1, b2.b2;
    
    connect a.a -> b;
    connect a.a -> b1, b2;
    
    connect a.a -> b.b;
    connect a.a -> b1.b1, b2.b2;
    

    <<someStereo, st2, st3 = "value">> connect a -> b;
    <<someStereo, st2, st3 = "value">> connect a -> b1, b2;
    
    <<someStereo, st2, st3 = "value">> connect a -> b.b;
    <<someStereo, st2, st3 = "value">> connect a -> b1.b1, b2.b2;
    
    <<someStereo, st2, st3 = "value">> connect a.a -> b;
    <<someStereo, st2, st3 = "value">> connect a.a -> b1, b2;
    
    <<someStereo, st2, st3 = "value">> connect a.a -> b.b;
    <<someStereo, st2, st3 = "value">> connect a.a -> b1.b1, b2.b2;    
    
    
}