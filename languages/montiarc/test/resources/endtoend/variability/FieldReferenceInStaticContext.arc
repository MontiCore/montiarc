/* (c) https://github.com/MontiCore/monticore */
package variability;

component FieldReferenceInStaticContext(int p = x) extends ParameterizedSuperComponent(x) {

  int x = 1;
  component Inner(int p) { }
  Inner sub(x);

  constraint(x > 0);
  varif(x > 0) { }

}
