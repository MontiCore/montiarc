/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package generation;

import de.monticore.types.types._ast.ASTType;

/**
 * TODO
 *
 * @author (last commit)
 * @version ,
 * @since TODO
 */
class Field {
  private final String name;
  private final ASTType type;

  Field(String name, ASTType type) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public ASTType getType() {
    return type;
  }
}
