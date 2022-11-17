/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

component MaxL {
  port <<sync>> in long a, b,
       <<sync>> out long r;

  compute {
    r = Math.max(a, b);
  }
}
