/* (c) https://github.com/MontiCore/monticore */
package prepost;

spec SumUp {

  port in int x;
  port out int y;

  int s = 0;

  ------------------------

  pre:  true;
  post: s = x+s@pre && y = s@pre;

}
