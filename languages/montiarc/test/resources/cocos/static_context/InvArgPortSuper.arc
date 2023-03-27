/* (c) https://github.com/MontiCore/monticore */
package static_context;

component InvArgPortSuper extends VNoPortRef {

  component Inner(int p) { }

  Inner inner(i);

}
