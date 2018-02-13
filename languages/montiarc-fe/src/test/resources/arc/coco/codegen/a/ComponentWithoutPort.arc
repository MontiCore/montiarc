package a;

/*
 * Valid model.
 *
 * @author Arne Haber
 */
component ComponentWithoutPort {
    
    component InnerWithPort {
        port 
            in String s1;
    }
    
    component InnerWithoutPort {
    
    }

}