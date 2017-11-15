package contextconditions.invalid;


component MultipleOutputsSamePort{

port in int i,
     out int o,
     out int x,
     out int y;

var int v;

automaton MultipleOutputsSamePort {

    state S;
    initial S;
    
    S [true] {v == 1} / {v=2, y=1, v=3, o = 3, o = 4, x = 1, x = 5};
}
}