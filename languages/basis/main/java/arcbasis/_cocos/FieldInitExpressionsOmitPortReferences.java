/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.util.IPortReferenceInExpressionExtractor;
import arcbasis._cocos.util.PortReferenceExtractor4ExpressionBasis;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static arcbasis._cocos.util.IPortReferenceInExpressionExtractor.PortReference;

/**
 * RRW14a R3: Variable declarations may not reference ports. As a convention, we require that initial value assignments
 * to variables are performed prior to any communication taking place. Thus, variable declarations may not reference any
 * port.
 */
public class FieldInitExpressionsOmitPortReferences implements ArcBasisASTComponentTypeCoCo {

  protected final IPortReferenceInExpressionExtractor portRefExtractor;

  public FieldInitExpressionsOmitPortReferences() {
    this.portRefExtractor = new PortReferenceExtractor4ExpressionBasis();
  }

  public FieldInitExpressionsOmitPortReferences(@NotNull IPortReferenceInExpressionExtractor portRefExtractor) {
    this.portRefExtractor = Preconditions.checkNotNull(portRefExtractor);
  }

  @Override
  public void check(@NotNull ASTComponentType astComp) {
    Preconditions.checkNotNull(astComp);
    Preconditions.checkArgument(astComp.isPresentSymbol());

    ComponentTypeSymbol comp = astComp.getSymbol();
    HashSet<PortReference> portReferencesToLookFor = new HashSet<>();

    portReferencesToLookFor.addAll(PortReference.ofComponentTypePorts(comp));
    portReferencesToLookFor.addAll(PortReference.ofSubComponentPorts(comp));

    List<VariableSymbol> fields = new ArrayList<>(comp.getFields());
    fields.removeAll(comp.getParameters()); // ComponentTypeSymbol::getFields also returns parameters


    for (VariableSymbol field : fields) {
      if (field.isPresentAstNode() && field.getAstNode() instanceof ASTArcField) {
        ASTArcField arcField = (ASTArcField) field.getAstNode();

        HashMap<PortReference, SourcePosition> foundPortReferences = this.portRefExtractor.findPortReferences(
          arcField.getInitial(), portReferencesToLookFor, ArcBasisMill.traverser()
        );

        for (PortReference illegalPortRef : foundPortReferences.keySet()) {
          SourcePosition illegalPortRefPosition = foundPortReferences.get(illegalPortRef);

          Log.error(ArcError.PORT_REFERENCE_IN_FIELD_INIT_EXPRESSION_ILLEGAL.format(
              arcField.getName(), comp.getFullName(), illegalPortRef.toString()),
            illegalPortRefPosition);
        }
      }
    }
  }
}
