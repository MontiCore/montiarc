package invalid;

component UseOfUndeclaredField{

   port 
     in int a,
     out int c;
   
   
   var int b;
 

automaton UseOfUndeclaredField{
    
    state A,B;
    
    initial A;
    
    A -> B {x == "Hallo Welt", y == 5};
    B -> A {a == 2, b == 3};
    A -> A {a == b};
    
}
}