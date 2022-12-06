/* (c) https://github.com/MontiCore/monticore */
package expressions;

component BinaryXor {

  port <<sync>> in int a, b;
  port <<sync>> out int r;

  compute {
    r = a ^ b;
  }
}
