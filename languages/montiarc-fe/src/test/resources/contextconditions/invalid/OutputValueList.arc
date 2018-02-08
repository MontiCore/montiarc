package contextconditions.invalid;


component OutputValueList{

port out Boolean b;

automaton OutputValueList {

  state S;
  initial S;

  S -> S / {b = [true, false, true]};

}
}