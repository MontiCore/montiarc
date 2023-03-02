/* (c) https://github.com/MontiCore/monticore */
import subcomponentDefinitions.*;

/**
 * Invalid model.
 */
component InvalidAssignmentSyntax {
  boolean b = true;
  Simple simple(a=b=true);
  Complex c(a=true, b+=5);
  Complex c2(a=true, 1 + 1);
}
