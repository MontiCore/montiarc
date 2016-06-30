package de.monticore.lang.montiarc.unit;

public interface Power extends Unit {
  default String getKind() {
    return "Power";
  }
}