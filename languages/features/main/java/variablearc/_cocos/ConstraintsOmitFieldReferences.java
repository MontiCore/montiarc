/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc._cocos.util.FieldReferenceExtractor4ExpressionBasis;
import variablearc._cocos.util.IFieldReferenceInExpressionExtractor;
import variablearc._cocos.util.IFieldReferenceInExpressionExtractor.FieldReference;

import java.util.HashMap;
import java.util.HashSet;

/**
 * As a convention, we require that instantiations are performed prior to any
 * communication taking place. Thus, constraints may not reference any ArcField.
 */
public class ConstraintsOmitFieldReferences implements ArcBasisASTComponentTypeCoCo {

  protected final IFieldReferenceInExpressionExtractor fieldRefExtractor;

  public ConstraintsOmitFieldReferences() {
    this.fieldRefExtractor = new FieldReferenceExtractor4ExpressionBasis();
  }

  @Override
  public void check(ASTComponentType astComp) {
    Preconditions.checkNotNull(astComp);
    Preconditions.checkArgument(astComp.isPresentSymbol());

    ComponentTypeSymbol comp = astComp.getSymbol();

    HashSet<FieldReference> portReferencesToLookFor = new HashSet<>(FieldReference.ofComponentTypeFields(comp));

    astComp.getBody().getArcElementList().stream()
      .filter(e -> e instanceof ASTArcConstraintDeclaration)
      .map(e -> (ASTArcConstraintDeclaration) e)
      .forEach((constraint) -> {
        Preconditions.checkNotNull(constraint);
        HashMap<FieldReference, SourcePosition> foundPortReferences =
          this.fieldRefExtractor.findFieldReferences(constraint.getExpression(), portReferencesToLookFor,
            VariableArcMill.traverser());

        for (FieldReference illegalFieldRef : foundPortReferences.keySet()) {
          SourcePosition illegalPortRefPosition = foundPortReferences.get(illegalFieldRef);
          Log.error(VariableArcError.FIELD_REFERENCE_IN_CONSTRAINT_ILLEGAL.format(illegalFieldRef.toString()),
            illegalPortRefPosition);
        }
      });
  }
}
