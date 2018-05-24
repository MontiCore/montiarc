package a;

/*
 *
 * TODO Check desired behaviour and complete annotation
 * TODO Add test
 */
component AutoInstantiateWarning {

    component Inner1 {
        //Empty body
    }

    // Threw warning in old MontiArc
    component InnerNot<T> {
        // Empty body
    }

    // Threw warning in old MontiArc
    component InnerNot2(int x) {
        // Empty body
    }
    
    component InnerNotAutomaticallyButManually(int x) {
        // Empty body
    }
    component InnerNotAutomaticallyButManually(4) one;
}