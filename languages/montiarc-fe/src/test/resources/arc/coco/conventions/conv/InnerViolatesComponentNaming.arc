package conv;

component InnerViolatesComponentNaming {
    
    port 
        in String s1;
    component violates {
        port 
            in String s2;
    }
    
    component violates v;
    connect s1 -> v.s2;
}