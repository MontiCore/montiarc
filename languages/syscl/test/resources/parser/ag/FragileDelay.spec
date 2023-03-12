/* (c) https://github.com/MontiCore/monticore */
package ag;

spec FragileDelay(in n) {

  port in TStream<D> i;
  port out TStream<D> o;

  assume: forall int j in N: i.sub(j + 1).length <= o.sub(j).length + 1;
  guarantee: forall int j in N : subset(o.sub(j+1), i.sub(j));

}
