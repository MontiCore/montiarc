/* (c) https://github.com/MontiCore/monticore */
package parser.statecharts.valid;

/**
 * valid
 * contains the most basic valid automaton
 */
component A_EmptyStateChart {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  automaton { }
}