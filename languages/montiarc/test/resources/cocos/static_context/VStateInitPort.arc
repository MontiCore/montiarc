/* (c) https://github.com/MontiCore/monticore */
package static_context;

component VStateInitPort {

  port in int i;
  port out int o;

  automaton {
    initial { o = i.T.a; } state s;
  }

}
