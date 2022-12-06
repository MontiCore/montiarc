/* (c) https://github.com/MontiCore/monticore */
package expressions;

component RightShift {

  port <<sync>> in int d, b;
  port <<sync>> out int r;

  compute {
    r = d >> b;
  }
}
