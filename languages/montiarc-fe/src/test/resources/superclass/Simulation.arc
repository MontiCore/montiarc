package superclass;

import superclass.Data.*;

component Simulation {
  port in MyMessage msgs;
  
  component ErrorFilter {
    port in  MyMessage msgs,
         out MyError errors;
  }
  
  component MessageDisplay {
    port in MyMessage msgs;
  }
  
  component MessageDisplay errorDisplay;
  
  connect ErrorFilter.errors -> errorDisplay.msgs;
}