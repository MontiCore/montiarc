package contextconditions.invalid;


component AutomatonOutputInExpression{

  var int v;

  port in int i,
       out int o;

  automaton AutomatonOutputInExpression{

     state S;
     initial S;

     S [i == v + i / o] {v == (o+1)*o} / {o = (v*i)+o};

  }
}