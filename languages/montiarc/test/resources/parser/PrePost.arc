/* (c) https://github.com/MontiCore/monticore */
package parser;

/*
 * Valid model using pre and postconditions.
 */
component PrePost {
  port in int a;
  port in int b;

  pre: a == b;
  post: a != b;
}
