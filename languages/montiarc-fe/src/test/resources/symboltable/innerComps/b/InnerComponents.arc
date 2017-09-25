package b;


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
    
   
   
}