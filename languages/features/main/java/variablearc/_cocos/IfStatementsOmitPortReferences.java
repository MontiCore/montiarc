/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbasis._cocos.util.IPortReferenceInExpressionExtractor;
import arcbasis._cocos.util.IPortReferenceInExpressionExtractor.PortReference;
import arcbasis._cocos.util.PortReferenceExtractor4ExpressionBasis;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import variablearc.VariableArcMill;
import variablearc._cocos.util.ComponentIfStatementHandler;

import java.util.HashMap;
import java.util.HashSet;

/**
 * As a convention, we require that instantiations are performed prior to any
 * communication taking place. Thus, if-statements may not reference any port.
 */
public class IfStatementsOmitPortReferences implements ArcBasisASTComponentTypeCoCo {

  protected final IPortReferenceInExpressionExtractor portRefExtractor;

  public IfStatementsOmitPortReferences() {
    this.portRefExtractor = new PortReferenceExtractor4ExpressionBasis();
  }

  @Override
  public void check(ASTComponentType astComp) {
    Preconditions.checkNotNull(astComp);
    Preconditions.checkArgument(astComp.isPresentSymbol(),
      "ASTComponentType node '%s' has no " + "symbol. Did you forget to run the SymbolTableCreator before checking cocos? Without symbol, we can not "
        + "check CoCo '%s'", astComp.getName(), this.getClass()
        .getSimpleName());

    ComponentTypeSymbol comp = astComp.getSymbol();
    HashSet<PortReference> portReferencesToLookFor = new HashSet<>();

    portReferencesToLookFor.addAll(PortReference.ofComponentTypePorts(comp));
    portReferencesToLookFor.addAll(PortReference.ofSubComponentPorts(comp));

    ComponentIfStatementHandler handler = new ComponentIfStatementHandler(astComp, (ifStatement) -> {
      Preconditions.checkNotNull(ifStatement);
      HashMap<PortReference, SourcePosition> foundPortReferences =
        this.portRefExtractor.findPortReferences(ifStatement.getCondition(), portReferencesToLookFor, VariableArcMill.traverser());

      for (PortReference illegalPortRef : foundPortReferences.keySet()) {
        SourcePosition illegalPortRefPosition = foundPortReferences.get(illegalPortRef);
        Log.error(VariableArcError.PORT_REFERENCE_IN_IF_STATEMENT_ILLEGAL.format(illegalPortRef.toString()), illegalPortRefPosition);
      }

    });
    astComp.accept(handler.getTraverser());
  }
}
