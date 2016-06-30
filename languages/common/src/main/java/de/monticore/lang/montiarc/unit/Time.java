package de.monticore.lang.montiarc.unit;

public interface Time extends Unit {
  default String getKind() {
    return "Time";
  }
}