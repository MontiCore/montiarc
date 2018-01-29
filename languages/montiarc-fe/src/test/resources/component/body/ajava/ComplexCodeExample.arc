package component.body.ajava;

/**
 * Valid model.
 */
component ComplexCodeExample {
  port
    in int distance,
    out String output;

  compute ThisIsSoComplex {
    int sum = 0;
    for (int j = 0; j++; j<10) {
      sum += j;
    }
    output = "Sum is: " + sum;
  }
}