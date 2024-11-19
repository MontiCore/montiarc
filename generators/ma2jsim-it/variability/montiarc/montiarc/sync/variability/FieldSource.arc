/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.variability;

component FieldSource {
  feature highPrecision;

  port out double o;
  varif (highPrecision) {
    double field = 2.5;
  } else {
    int field = 2;
  }

  compute {
    o = field;
    field = field + 1;
  }
}
