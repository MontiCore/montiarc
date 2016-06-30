package pretty;

component References {

    component SimpleReference;
    
    <<someStereo, st2, st3 = "value">> component SimpleReferenceWithStereo;
    
    component NamedReference nr1, nr2;
    
    component NamedReferenceWithSimpleConnectors
        nrwsc1 [out1 -> asdf1, asdf2, asdf3; out2 -> asdf4; out3 -> nr1.in1],
        nrwsc2 [out1 -> nr2.in1];
    
    <<someStereo, st2, st3 = "value">> component NamedReferenceWithStereo nr1, nr2;
    
    <<someStereo, st2, st3 = "value">> component NamedReferenceWithSimpleConnectorsWithStereo
        nrwsc1 [out1 -> asdf1, asdf2, asdf3; out2 -> asdf4; out3 -> nr1.in1],
        nrwsc2 [out1 -> nr2.in1];
    
}