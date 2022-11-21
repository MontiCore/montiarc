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
import variablearc._ast.ASTArcConstraintDeclaration;

import java.util.HashMap;
import java.util.HashSet;

/**
 * As a convention, we require that instantiations are performed prior to any
 * communication taking place. Thus, constraints may not reference any port.
 */
public class ConstraintsOmitPortReferences implements ArcBasisASTComponentTypeCoCo {

  protected final IPortReferenceInExpressionExtractor portRefExtractor;

  public ConstraintsOmitPortReferences() {
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

    astComp.getBody().getArcElementList().stream()
      .filter(e -> e instanceof ASTArcConstraintDeclaration)
      .map(e -> (ASTArcConstraintDeclaration) e)
      .forEach((constraint) -> {
        Preconditions.checkNotNull(constraint);
        HashMap<PortReference, SourcePosition> foundPortReferences =
          this.portRefExtractor.findPortReferences(constraint.getExpression(), portReferencesToLookFor, VariableArcMill.traverser());

        for (PortReference illegalPortRef : foundPortReferences.keySet()) {
          SourcePosition illegalPortRefPosition = foundPortReferences.get(illegalPortRef);
          Log.error(VariableArcError.PORT_REFERENCE_IN_CONSTRAINT_ILLEGAL.format(illegalPortRef.toString()), illegalPortRefPosition);
        }
      });
  }
}