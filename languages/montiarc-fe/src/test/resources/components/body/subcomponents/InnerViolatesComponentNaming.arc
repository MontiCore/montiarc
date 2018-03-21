package components.body.subcomponents;

/*
 * Invalid model.
 * Component names have to start with a capital letter
 *
 * @implements No Literature
 */
component InnerViolatesComponentNaming {
    
    port 
        in String s1;
    component violates { // Component name does not start with a capital letter
        port 
            in String s2;
    }
    
    component violates v; // Component name does not start with a capital letter
    connect s1 -> v.s2;
}