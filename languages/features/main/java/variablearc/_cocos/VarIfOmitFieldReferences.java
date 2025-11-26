/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import variablearc.VariableArcMill;
import variablearc._cocos.util.ComponentVarIfHandler;
import variablearc._cocos.util.FieldReferenceExtractor4ExpressionBasis;
import variablearc._cocos.util.IFieldReferenceInExpressionExtractor;
import variablearc._cocos.util.IFieldReferenceInExpressionExtractor.FieldReference;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * As a convention, we require that instantiations are performed prior to any
 * communication taking place. Thus, if-statements may not reference any ArcField.
 */
public class VarIfOmitFieldReferences implements ArcBasisASTArcComponentTypeCoCo {

  protected final IFieldReferenceInExpressionExtractor fieldRefExtractor;

  public VarIfOmitFieldReferences() {
    this.fieldRefExtractor = new FieldReferenceExtractor4ExpressionBasis();
  }

  @Override
  public void check(ASTArcComponentType astComp) {
    Preconditions.checkNotNull(astComp);
    Preconditions.checkArgument(astComp.isPresentSymbol());

    ComponentTypeSymbol comp = astComp.getSymbol();

    LinkedHashSet<FieldReference> fieldReferencesToLookFor = new LinkedHashSet<>(FieldReference.ofComponentTypeFields(comp));

    ComponentVarIfHandler handler = new ComponentVarIfHandler(astComp, (varif) -> {
      Preconditions.checkNotNull(varif);
      LinkedHashMap<FieldReference, SourcePosition> foundPortReferences =
        this.fieldRefExtractor.findFieldReferences(varif.getCondition(), fieldReferencesToLookFor,
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
