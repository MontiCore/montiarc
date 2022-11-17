/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

component AbsI {
  port <<sync>> in int a,
       <<sync>> out int r;
  port <<sync>> out boolean of;

  compute {
    r = Math.abs(a);
    of = a == Integer.MIN_VALUE;
  }
}
