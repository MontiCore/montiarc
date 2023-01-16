/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import variablearc._cocos.util.IFieldReferenceInExpressionExtractor;
import arcbasis._symboltable.ComponentTypeSymbol;
import variablearc._cocos.util.IFieldReferenceInExpressionExtractor.FieldReference;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import variablearc.VariableArcMill;
import variablearc._cocos.util.ComponentIfStatementHandler;
import variablearc._cocos.util.FieldReferenceExtractor4ExpressionBasis;

import java.util.HashMap;
import java.util.HashSet;

/**
 * As a convention, we require that instantiations are performed prior to any
 * communication taking place. Thus, if-statements may not reference any ArcField.
 */
public class IfStatementsOmitFieldReferences implements ArcBasisASTComponentTypeCoCo {

  protected final IFieldReferenceInExpressionExtractor fieldRefExtractor;

  public IfStatementsOmitFieldReferences() {
    this.fieldRefExtractor = new FieldReferenceExtractor4ExpressionBasis();
  }

  @Override
  public void check(ASTComponentType astComp) {
    Preconditions.checkNotNull(astComp);
    Preconditions.checkArgument(astComp.isPresentSymbol(),
      "ASTComponentType node '%s' has no " +
        "symbol. Did you forget to run the SymbolTableCreator before checking cocos? Without symbol, we can not "
        + "check CoCo '%s'", astComp.getName(), this.getClass()
        .getSimpleName());

    ComponentTypeSymbol comp = astComp.getSymbol();

    HashSet<FieldReference> portReferencesToLookFor = new HashSet<>(FieldReference.ofComponentTypeFields(comp));

    ComponentIfStatementHandler handler = new ComponentIfStatementHandler(astComp, (ifStatement) -> {
      Preconditions.checkNotNull(ifStatement);
      HashMap<FieldReference, SourcePosition> foundPortReferences =
        this.fieldRefExtractor.findFieldReferences(ifStatement.getCondition(), portReferencesToLookFor,
          VariableArcMill.traverser());

      for (FieldReference illegalFieldRef : foundPortReferences.keySet()) {
        SourcePosition illegalPortRefPosition = foundPortReferences.get(illegalFieldRef);
        Log.error(VariableArcError.FIELD_REFERENCE_IN_IF_STATEMENT_ILLEGAL.format(illegalFieldRef.toString()),
          illegalPortRefPosition);
      }

    });
    astComp.accept(handler.getTraverser());
  }
}
