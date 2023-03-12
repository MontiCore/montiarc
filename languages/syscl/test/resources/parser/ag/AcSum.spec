/* (c) https://github.com/MontiCore/monticore */
package ag;

spec AcSum {

  port in UStream<Integer> i;
  port out UStream<Integer> o;

  guarantee: i.length = o.length && forall int j in dom(o) : o.j == sum(i);

}
