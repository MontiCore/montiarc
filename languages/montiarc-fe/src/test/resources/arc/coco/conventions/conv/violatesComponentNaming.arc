package conv;

component violatesComponentNaming {
    port 
        in String s1;
    component IsCorrect {
        port 
            in String s2;
    }
    
    component IsCorrect isCorrect;
    connect s1 -> isCorrect.s2;
}