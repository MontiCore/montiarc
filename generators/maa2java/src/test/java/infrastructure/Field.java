/* (c) https://github.com/MontiCore/monticore */
/*
 *
 * http://www.se-rwth.de/
 */
package infrastructure;

import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;

/**
 * TODO
 *
 * @author (last commit)
 * @version ,
 * @since TODO
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
