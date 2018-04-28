package a;

component AutoInstantiateWarning {

    
    component Inner1 {
        
    }
    
    component InnerNot<T> {
        
    }
    
    component InnerNot2(int x) {
        
    }
    
    component InnerNotAutomaticallyButManually(int x) {
        
    }
    component InnerNotAutomaticallyButManually(4) one;
}