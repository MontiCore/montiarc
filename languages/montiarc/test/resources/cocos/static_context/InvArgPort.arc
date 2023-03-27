/* (c) https://github.com/MontiCore/monticore */
package static_context;

component InvArgPort {

  port in int i;

  component Inner(int p) { }

  Inner inner(i);

}
