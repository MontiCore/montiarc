/* (c) https://github.com/MontiCore/monticore */
package components;

/** Invalid model: raw usage of generic data and component types. */
component QualifiedTypeRawType(java.util.List list) {
  port out java.util.List output;
  java.util.List field = null;

  component Inner<T> { }
  Inner instance;
}
