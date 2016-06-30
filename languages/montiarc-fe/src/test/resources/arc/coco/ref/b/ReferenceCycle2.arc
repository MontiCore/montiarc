package b;

import a.*;

component ReferenceCycle2 {
    port 
        in Integer portIn;
        
    component ReferenceCycle refCycle;
    
    connect portIn -> refCycle.portIn;
}