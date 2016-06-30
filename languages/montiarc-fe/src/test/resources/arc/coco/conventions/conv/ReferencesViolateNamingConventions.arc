package conv;

component ReferencesViolateNamingConventions {
    port 
        in String s1;
    
    component violatesComponentNaming;
    
    component Ref Violates, doesNotViolate;
    
    connect s1 -> violatesComponentNaming.s1, Violates.s1, doesNotViolate.s1;
    
}