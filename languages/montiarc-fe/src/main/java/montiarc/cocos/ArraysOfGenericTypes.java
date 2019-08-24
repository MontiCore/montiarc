/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import de.monticore.types.types._ast.ASTComplexArrayType;
import de.monticore.types.types._ast.ASTComplexReferenceType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTTypeArguments;
import de.monticore.types.types._cocos.TypesASTComplexArrayTypeCoCo;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTVariableDeclaration;
import montiarc._cocos.MontiArcASTVariableDeclarationCoCo;

import java.util.List;

/**
 * Checks that there are no variable, parameter or port types which are
 * arrays of generic types with type arguments.
 *
 * @author Michael Mutert
 */
public class ArraysOfGenericTypes implements TypesASTComplexArrayTypeCoCo {
  @Override
  public void check(ASTComplexArrayType node) {
    if(node.getDimensions() > 0){
      if(node.getComponentType() instanceof ASTComplexReferenceType){
        final List<ASTSimpleReferenceType> simpleReferenceTypeList =
            ((ASTComplexReferenceType) node.getComponentType()).getSimpleReferenceTypeList();
        for (ASTSimpleReferenceType simpleRefType : simpleReferenceTypeList) {
          if(simpleRefType.getTypeArgumentsOpt().isPresent()){
            Log.error("0xMA048 Arrays of generic types are forbidding."); // TODO Error message
          }
        }
      }
    }
  }
}
