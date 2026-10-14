/* (c) https://github.com/MontiCore/monticore */
package features;

component D {
  feature f;
  varif (f) {
    A a;
  }
  constraint (a.f);
}
