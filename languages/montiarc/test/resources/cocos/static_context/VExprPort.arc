/* (c) https://github.com/MontiCore/monticore */
package static_context;

component VExprPort {

  port in int i;

  int var = 1 + i.T.a;

}
