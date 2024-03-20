/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. The subcomponent's type cannot be resolved. The tool
 * should report an error for the missing type. However, the tool should end
 * gracefully even when referencing the subcomponent in an expression.
 */
component MissingCompType5 {

  Missing sub;

  constraint(sub.f == true);
}
