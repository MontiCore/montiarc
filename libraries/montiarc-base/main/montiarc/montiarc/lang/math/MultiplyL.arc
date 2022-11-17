/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

component MultiplyL {
  port <<sync>> in long a, b,
       <<sync>> out long r;
  port <<sync>> out boolean of;

  compute {
    long _r = a * b;
    long aa = Math.abs(a);
    long ab = Math.abs(b);
    of = ((aa | ab) >>> 31 != 0) ? (((b != 0) && (_r / b != a)) || (a == Long.MIN_VALUE && b == -1)) : false;
    r = _r;
  }
}
