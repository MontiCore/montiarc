/* (c) https://github.com/MontiCore/monticore */
package inheritance;

/**
 * Component with inner subcomponent that extends another inner component
 */
component InnerInheritance {
  component A(int i = 0) {
  }

  component B(int i = 1) extends A(i) {

  }
}
