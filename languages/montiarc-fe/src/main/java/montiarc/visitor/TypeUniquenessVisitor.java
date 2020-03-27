/* (c) https://github.com/MontiCore/monticore */
package montiarc.visitor;

import de.monticore.types.types._ast.ASTTypeParameters;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._visitor.MontiArcVisitor;

import java.util.*;

/**
 * Used to check that there are no component type variables that are reused
 * as type variables in inner component definitions.
 *
 */
public class TypeUniquenessVisitor implements MontiArcVisitor {

  private Deque<String> typeParameterNames = new ArrayDeque<>();

  @Override
  public void visit(ASTComponent node){
    final Optional<ASTTypeParameters> genericTypeParametersOpt
        = node.getHead().getGenericTypeParametersOpt();
    List<String> localTypeParams = new ArrayList<>();

    // Determine all local type parameter names
    genericTypeParametersOpt.ifPresent(
        params -> params
                      .getTypeVariableDeclarationList()
                      .forEach(param -> localTypeParams.add(param.getName())));

    // Check for occurence and push onto stack
    for (String typeParam : localTypeParams) {
      if(typeParameterNames.contains(typeParam)){
        Log.error(
            String.format("0xMA114 The type parameter %s is reused in " +
                              "the component%s",
                typeParam, node.getName()),
            node.get_SourcePositionStart());
      }
      typeParameterNames.push(typeParam);
    }
  }

  @Override
  public void endVisit(ASTComponent node){

    // Remove type parameter names from stack after visiting the node in DFS.
    if(node.getHead().getGenericTypeParametersOpt().isPresent())
      for (ASTTypeVariableDeclaration decl :
          node.getHead().getGenericTypeParameters().getTypeVariableDeclarationList()) {
        typeParameterNames.pop();
      }

  }
}
