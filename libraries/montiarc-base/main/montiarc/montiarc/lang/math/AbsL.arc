/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

component AbsL {
  port <<sync>> in long a,
       <<sync>> out long r;
  port <<sync>> out boolean of;

  compute {
    r = Math.abs(a);
    of = a == Long.MIN_VALUE;
  }
}
