package components.body.invariants;

/**
 * Valid model.
 */
component OCLFieldToArcField(int foo) {
  ocl inv myInv:
    foo <= 5;
}
