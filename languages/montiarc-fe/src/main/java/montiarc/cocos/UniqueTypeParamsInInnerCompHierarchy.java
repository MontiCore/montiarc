/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import de.monticore.types.types._ast.ASTTypeParameters;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._visitor.MontiArcVisitor;

import java.util.*;

/**
 * Check that inner components do not reuse type parameter names from the defining
 * components hierarchy
 * @author Michael Mutert
 */
public class UniqueTypeParamsInInnerCompHierarchy implements MontiArcASTComponentCoCo {


  @Override
  public void check(ASTComponent node) {
    new TypeUniquenessVisitor().handle(node);
  }

  class TypeUniquenessVisitor implements MontiArcVisitor {
    Deque<String> typeParameterNames = new ArrayDeque<>();

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
}
