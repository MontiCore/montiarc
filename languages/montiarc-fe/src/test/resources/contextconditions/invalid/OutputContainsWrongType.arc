package contextconditions.invalid;


component OutputContainsWrongType{

port out Boolean b;

automaton OutputContainsWrongType {

  state S;
  initial S;
    
  S -> S / {b = 5};

}
}