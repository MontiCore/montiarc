/* (c) https://github.com/MontiCore/monticore */
package transformations.afterParsingTrafos;

/**
 * Valid model.
 */
component A {
  port out int outP;

  B b [fooP -> outP;];

  component Foo {
    port out int fooP;
  }
}
