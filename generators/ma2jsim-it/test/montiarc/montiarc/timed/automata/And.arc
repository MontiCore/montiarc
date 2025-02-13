package montiarc.timed.automata;

component And {

port in boolean a;
port in boolean b;
port out boolean q;

boolean as = false;
boolean bs = false;

automaton {
  initial state S;
  S -> S a / { as = a; };
  S -> S b / { bs = b; };
  S -> S / { q = as && bs; };
  }
}