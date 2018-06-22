package components.body.subcomponents;

/**
* Invalid model. See comments below.
* @implements R13: Subcomponent reference cycles are forbidden. (p. 68, lst. 3.48)
* TODO Add test
*/
component ReferenceCycle {

    port
        in Integer portIn;
    
    component Inner1 {
        port 
            in Integer portIn;
            
        // cycle 1
        component ReferenceCycle refCycle1;
        connect portIn -> refCycle1.portIn;
    }
    
    component Inner2 {
        port 
            in Integer portIn;
        
        //cycle 2
        component Inner1 refCycle2;
        connect portIn -> refCycle2.portIn;
    }
    
    component Inner3 {
        port
            in Integer portIn;

        //cycle 3
        component ReferenceCycle2 myComp2;
        connect portIn -> myComp2.portIn;
    }
    
    // cycle 4
    component Inner1 myInner1;
    // cycle 5
    component ReferenceCycle2 myComp2;
    
    connect portIn -> myInner1.portIn, myComp2.portIn;
}