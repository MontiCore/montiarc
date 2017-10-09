package conv;

component ReferencesViolateNamingConventions {
    port 
        in String s1;
    
    component violatesComponentNaming; // Component name does not start with a capital letter
    
    component Ref Violates, // Reference name starts with a capital letter
        doesNotViolate;
    
    connect s1 -> violatesComponentNaming.s1, Violates.s1, doesNotViolate.s1;
    
}