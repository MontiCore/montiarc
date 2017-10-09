package conv;

component violatesComponentNaming {  // Name of the component is not capitalized
    port 
        in String s1;
    component IsCorrect {
        port 
            in String s2;
    }
    
    component IsCorrect isCorrect;
    connect s1 -> isCorrect.s2;
}