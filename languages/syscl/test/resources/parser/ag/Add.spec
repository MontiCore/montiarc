/* (c) https://github.com/MontiCore/monticore */
package ag;

spec Add {

  port in UStream<Integer> i1, i2;
  port out UStream<Integer> o;

  guarantee: o.length = min(i1.length, i2.length) && forall int j in dom(o) : o.j == i1.j + i2.j;

}
