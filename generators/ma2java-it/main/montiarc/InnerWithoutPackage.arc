/* (c) https://github.com/MontiCore/monticore */
component InnerWithoutPackage {
  // Valid model.
  // In  a previous version of the generator, using inner components within
  // components that do not have a package cause uncompilable code to be generated.
  component Inner i {}
}
