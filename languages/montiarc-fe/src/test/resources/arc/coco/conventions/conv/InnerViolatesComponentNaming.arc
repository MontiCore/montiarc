package conv;

component InnerViolatesComponentNaming {
    
    port 
        in String s1;
    component violates { // Inner component does not start with a capital letter
        port 
            in String s2;
    }
    
    component violates v; // Component name does not start with a capital letter
    connect s1 -> v.s2;
}