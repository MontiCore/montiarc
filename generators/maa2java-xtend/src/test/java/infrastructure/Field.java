/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package infrastructure;

/**
 * Represents a field that is expected to be in a generated file
 *
 * @author Michael Mutert
 */
public class Field {
  private final String name;
  private final String type;

  public Field(String name, String type) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  @Override public String toString() {
    return "Field{" +
        "name='" + name + '\'' +
        ", type=" + type +
        '}';
  }
}
