package components.body.ajava;

/*
 * Valid model.
 */
component ComplexCodeExample {
  port
    in int distance,
    out String output;

  compute ThisIsSoComplex {
    int sum = 0;
    for (int j = 0; j<10; j++) {
      sum += j;
    }
    output = "Sum is: " + sum;
  }
}