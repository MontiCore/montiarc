/* (c) https://github.com/MontiCore/monticore */
package prepost;

spec Adder {

  port in int x, in int y;
  port out int z;

  ------------------------

  pre:  true
  post: z = x + y

}
