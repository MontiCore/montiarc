/* (c) https://github.com/MontiCore/monticore */
component CarExample {

  // All Features
  feature car, body, engine, gear, manual, automatic, electric, gas, powerLocks, keylessEntry;

  // Child Parents
  constraint ((!electric || engine) && (!gas || engine) && (!manual || gear) && (!automatic || gear));

  // Mandatory
  constraint (car && body && engine && gear);

  // Or-Group
  constraint (!engine || (electric || gas));

  // XOR-Group
  constraint (!gear || ((manual || automatic) && !(manual && automatic)));

  // Additional
  constraint (!keylessEntry || powerLocks);

  // c && b && n && r && (m || a) && !(m && a) && (e || g) && (!k || p));
  //  && (!c || b) && (!c || n) && (!c ||r)

  // Test Constraint
  //constraint ((!a || b) && (b == b) && c && !(c == d) && (a || b || c));
  //constraint ((!a || a || !b || c || !d) && (a || !a) && !(a || e) && f && (1 == 1));

  //constraint ((a || b) && !(a && b));

  //constraint ((a || b || c) && (!a || !b) && (!a || !c) && (!b || !c) && (d || e) && !(d && e) && (f || !f) && (a || b || d));


  // Should be equal to (a XOR b XOR c XOR d) && g && simpleOr(e || f) && optional(h)
  //constraint ((a || b || c || d) && (!a || !b) && (!a || !c) && (!a || !d) && (!b || !c) && (!b || !d) && (!c || !d) && g && (e || f) && (h || !h));

  // Result should be optional(a) && optional(b) && c && d && (f XOR g)
  //constraint ((a || !a) && (b || !b) && c && d && e && (f || g) && !(f && g) && !h);

  // constraint ((!a || b || !c || i) && (!d || !e) && f && (g || h) && !(g && h) && (j || !j) && (2 > 0));

  // TEST CASES:
  // Test No Constraints

  // Test only Numerical Constraint (= Empty after Simplifying)
  //constraint ((3 > 2) && (5 <= (3 * 4)));

  //constraint (((3 > 2) ? a : b) && (5 > 2));

  //constraint (a);

  //constraint ((a || b) && !(a || b)); // Should yield an error since UNSAT

  //constraint ((b || a) && !(a && b));

  // constraint (!(a && b && c));

  // constraint (c || !a || !b);

  // constraint (a || !a);

}
