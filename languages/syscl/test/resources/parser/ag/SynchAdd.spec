/* (c) https://github.com/MontiCore/monticore */
package ag;

spec SynchAdd {

  port in TSStream<Integer> i1, i2;
  port out TSStream<Integer> o;

  guarantee: forall int j in N : o.j == i1.j + i2.j;

}
