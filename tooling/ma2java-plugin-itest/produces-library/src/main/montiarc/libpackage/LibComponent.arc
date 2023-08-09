/* (c) https://github.com/MontiCore/monticore */
package libpackage;

component LibComponent {
  port <<sync>> in int incoming;
  port <<sync>> out int outgoing;

  compute {
    outgoing = incoming + 1;
  }
}