/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.util.IPortReferenceInExpressionExtractor;
import arcbasis._cocos.util.PortReferenceExtractor4ExpressionBasis;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.HashMap;
import java.util.HashSet;

import static arcbasis._cocos.util.IPortReferenceInExpressionExtractor.PortReference;

/**
 * Arguments for component instantiations may not reference ports. As a convention, we require that component
 * instantiations are performed prior to any communication taking place. Thus, variable declarations may not reference
 * any port.
 */
public class ComponentArgumentsOmitPortRef implements ArcBasisASTComponentTypeCoCo {

  protected final IPortReferenceInExpressionExtractor portRefExtractor;

  public ComponentArgumentsOmitPortRef() {
    this(new PortReferenceExtractor4ExpressionBasis());
  }

  public ComponentArgumentsOmitPortRef(@NotNull IPortReferenceInExpressionExtractor portRefExtractor) {
    this.portRefExtractor = Preconditions.checkNotNull(portRefExtractor);
  }

  @Override
  public void check(@NotNull ASTComponentType astComp) {
    Preconditions.checkNotNull(astComp);
    Preconditions.checkArgument(astComp.isPresentSymbol());

    ComponentTypeSymbol comp = astComp.getSymbol();
    HashSet<IPortReferenceInExpressionExtractor.PortReference> portReferencesToLookFor = new HashSet<>();

    portReferencesToLookFor.addAll(IPortReferenceInExpressionExtractor.PortReference.ofComponentTypePorts(comp));
    portReferencesToLookFor.addAll(IPortReferenceInExpressionExtractor.PortReference.ofSubComponentPorts(comp));

    for (ComponentInstanceSymbol subComp : comp.getSubComponents()) {
      for(ASTArcArgument arg : subComp.getArcArguments()) {
        HashMap<PortReference, SourcePosition> foundPortReferences =
          this.portRefExtractor.findPortReferences(arg.getExpression(), portReferencesToLookFor, ArcBasisMill.traverser());

        for(PortReference portRef : foundPortReferences.keySet()) {
          SourcePosition portRefLoc = foundPortReferences.get(portRef);

          Log.error(ArcError.COMP_ARG_PORT_REF.toString(), portRefLoc);
        }
      }
    }
  }
}
