/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.util.IPortReferenceInExpressionExtractor;
import arcbasis._cocos.util.IPortReferenceInExpressionExtractor.PortReference;
import arcbasis._cocos.util.PortReferenceExtractor4ExpressionBasis;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * As a convention, we require that instantiations are performed prior to any communication taking place. Thus, default
 * values for configuration parameters may not reference any port.
 */
public class ParameterDefaultValueOmitsPortRef implements arcbasis._cocos.ArcBasisASTComponentTypeCoCo {

  protected final IPortReferenceInExpressionExtractor portRefExtractor;

  public ParameterDefaultValueOmitsPortRef() {
    this.portRefExtractor = new PortReferenceExtractor4ExpressionBasis();
  }

  public ParameterDefaultValueOmitsPortRef(@NotNull IPortReferenceInExpressionExtractor portRefExtractor) {
    this.portRefExtractor = Preconditions.checkNotNull(portRefExtractor);
  }


  @Override
  public void check(ASTComponentType astComp) {
    Preconditions.checkNotNull(astComp);
    Preconditions.checkArgument(astComp.isPresentSymbol());

    ComponentTypeSymbol comp = astComp.getSymbol();
    HashSet<PortReference> portReferencesToLookFor = new HashSet<>();

    portReferencesToLookFor.addAll(PortReference.ofComponentTypePorts(comp));
    portReferencesToLookFor.addAll(PortReference.ofSubComponentPorts(comp));

    List<VariableSymbol> params = comp.getParameters();

    for (VariableSymbol param : params) {
      Preconditions.checkState(param.isPresentAstNode());
      Preconditions.checkState(param.getAstNode() instanceof ASTArcParameter);
      ASTArcParameter astParam = (ASTArcParameter) param.getAstNode();

      if(astParam.isPresentDefault()) {
        HashMap<PortReference, SourcePosition> foundPortReferences = this.portRefExtractor.findPortReferences(
          astParam.getDefault(), portReferencesToLookFor, ArcBasisMill.traverser()
        );

        for (PortReference illegalPortRef : foundPortReferences.keySet()) {
          SourcePosition illegalPortRefPosition = foundPortReferences.get(illegalPortRef);
          Log.error(ArcError.PORT_REF_DEFAULT_VALUE.format(
            astParam.getName(), comp.getFullName(), illegalPortRef.toString()),
            illegalPortRefPosition);
        }
      }
    }
  }
}
