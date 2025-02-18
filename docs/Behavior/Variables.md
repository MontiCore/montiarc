<!-- (c) https://github.com/MontiCore/monticore -->

# Component variables

Component variables extend a component's state space. They can store messages 
and intermediate computation results to record state that is maintained across 
individual execution steps. 

Component variables are declared directly within the body of a component. 
The declaration of a component variable specifies the variable's type, states 
its name, and assigns its initial value. A component-variable declaration looks 
like 

```montiarc
TYPE NAME = INITIAL; 
```

where 

* `TYPE` is the variable's data type (reference)

* `NAME` is the variable's name (defining)

* `INITIAL` is an expression defining the variable's initial value

For example,

```montiarc
component SumUp {
  port in int i;
  port in Signal r;
  port out int o;
  
  int sum = 0;
  
  automaton {
    initial state S;
    S -> S i / {
      sum = sum + i;
    };
    S -> S r / {
      o = sum;
    };
  }
}
```

calculates the sum over past inputs using component variable `sum` of type 
`int`. The variable's initial value is `0`. Every time the component receives 
a message on port `i` it adds it to the value of `sum` and stores the new value 
in variable `sum`. When the component receives a message on port `r` it 
outputs the total sum calculated so far on port `o`.
