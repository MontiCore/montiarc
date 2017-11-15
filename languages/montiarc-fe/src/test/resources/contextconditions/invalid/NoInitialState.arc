package contextconditions.invalid;

component NoInitialState{

port in int a,
     out int b;

automaton NoInitialState {
    
    state S;   
}
}