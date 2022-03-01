/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.util.IPortReferenceInExpressionExtractor;
import static arcbasis._cocos.util.IPortReferenceInExpressionExtractor.PortReference;
import arcbasis._cocos.util.PortReferenceExtractor4ExpressionBasis;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Arguments for component instantiations may not reference ports. As a convention, we require that component
 * instantiations are performed prior to any communication taking place. Thus, variable declarations may not reference
 * any port.
 */
public class InstanceArgsOmitPortReferences implements ArcBasisASTComponentTypeCoCo {

  protected final IPortReferenceInExpressionExtractor portRefExtractor;

  public InstanceArgsOmitPortReferences() {
    this(new PortReferenceExtractor4ExpressionBasis());
  }

  public InstanceArgsOmitPortReferences(@NotNull IPortReferenceInExpressionExtractor portRefExtractor) {
    this.portRefExtractor = Preconditions.checkNotNull(portRefExtractor);
  }

  @Override
  public void check(@NotNull ASTComponentType astComp) {
    Preconditions.checkNotNull(astComp);
    Preconditions.checkArgument(astComp.isPresentSymbol(), "ASTComponentType node '%s' has no " +
        "symbol. Did you forget to run the SymbolTableCreator before checking cocos? Without symbol, we can not " +
        "check CoCo '%s'",
      astComp.getName(), this.getClass().getSimpleName());

    ComponentTypeSymbol comp = astComp.getSymbol();
    HashSet<IPortReferenceInExpressionExtractor.PortReference> portReferencesToLookFor = new HashSet<>();

    portReferencesToLookFor.addAll(IPortReferenceInExpressionExtractor.PortReference.ofComponentTypePorts(comp));
    portReferencesToLookFor.addAll(IPortReferenceInExpressionExtractor.PortReference.ofSubComponentPorts(comp));

    for (ComponentInstanceSymbol subComp : comp.getSubComponents()) {
      for(ASTExpression arg : subComp.getArguments()) {
        HashMap<PortReference, SourcePosition> foundPortReferences =
          this.portRefExtractor.findPortReferences(arg, portReferencesToLookFor, ArcBasisMill.traverser());

        for(PortReference portRef : foundPortReferences.keySet()) {
          SourcePosition portRefLoc = foundPortReferences.get(portRef);

          Log.error(ArcError.PORT_REFERENCE_IN_INSTANTIATION_ARG_ILLEGAL.format(subComp.getName(), portRef.toString()),
            portRefLoc);
        }
      }
    }
  }
}
