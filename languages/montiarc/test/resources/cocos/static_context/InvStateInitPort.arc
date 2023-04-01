/* (c) https://github.com/MontiCore/monticore */
package static_context;

component InvStateInitPort {

  port in int i;
  port out int o;

  automaton {
    initial { o = i; } state s;
  }

}
