/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package generation;

import de.monticore.types.types._ast.ASTSimpleReferenceType;
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

  @Override public String toString() {
    String typeString = type.toString();
    if(type instanceof ASTSimpleReferenceType){
      final ASTSimpleReferenceType type = (ASTSimpleReferenceType) this.type;
      typeString += type.getNameList();
      if(type.getTypeArgumentsOpt().isPresent()){
        typeString += "TypeArguments: " + type.getTypeArguments().toString();
      }
    }
    return "Field{" +
        "name='" + name + '\'' +
        ", type=" + typeString +
        '}';
  }
}
