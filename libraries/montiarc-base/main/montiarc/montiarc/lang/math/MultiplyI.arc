/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

component MultiplyI {
  port <<sync>> in int a, b,
       <<sync>> out int r;
  port <<sync>> out boolean of;

  compute {
    int _r = a * b;
    int aa = Math.abs(a);
    int ab = Math.abs(b);
    of = ((aa | ab) >>> 15 != 0) ? (((b != 0) && (_r / b != a)) || (a == Integer.MIN_VALUE && b == -1)) : false;
    r = _r;
  }
}
