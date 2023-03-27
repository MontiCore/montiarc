/* (c) https://github.com/MontiCore/monticore */
package static_context;

component VFInit {

  port in int i;

  int var = i.T.a;

}
