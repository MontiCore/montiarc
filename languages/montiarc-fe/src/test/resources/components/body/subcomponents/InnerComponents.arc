package components.body.subcomponents;

/**
 * Valid model. 
 */
component InnerComponents {
    component SimpleComponentWithAutomaton ref;  
    component Inner ref1;
    component InnerInner ref2; 

    component Inner {
        component SimpleComponentWithAutomaton ref;
        
         component InnerInner {
            component SimpleComponentWithAutomaton ref;
            
         }
         component InnerInner ref1;
    }

    component InnerWithoutPorts {
      //Empty body
    }
}