/* (c) https://github.com/MontiCore/monticore */
package static_context;

component InvExprPort {

  port in int i;

  int var = 1 + i;

}
