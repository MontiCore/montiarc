package a;

component ComponentWithoutPort {
    
    component InnerWithPort {
        port 
            in String s1;
    }
    
    component InnerWithoutPort {
    
    }

}