/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

component MinL {
  port <<sync>> in long a, b,
       <<sync>> out long r;

  compute {
    r = Math.min(a, b);
  }
}
