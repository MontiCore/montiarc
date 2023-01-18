/* (c) https://github.com/MontiCore/monticore */
package innerComponentNotExtendsDefiningComponent;

component InnerComponentExtendsDefiningComponent {
  component Inner extends InnerComponentExtendsDefiningComponent { }
}
