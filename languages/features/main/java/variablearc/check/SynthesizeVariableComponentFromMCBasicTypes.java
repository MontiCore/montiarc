/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.SynthCompTypeResult;
import arcbasis.check.SynthesizeComponentFromMCBasicTypes;
import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcScope;

import java.util.List;

/**
 * A visitor (a handler indeed) that creates {@link TypeExprOfVariableComponent}s from {@link ASTMCQualifiedType}s, given that
 * there is a ComponentTypeSymbol which is resolvable through the name represented by the {@link ASTMCQualifiedType}.
 */
public class SynthesizeVariableComponentFromMCBasicTypes extends SynthesizeComponentFromMCBasicTypes {

  public SynthesizeVariableComponentFromMCBasicTypes(SynthCompTypeResult resultWrapper) {
    super(resultWrapper);
  }

  @Override
  public void handle(@NotNull ASTMCQualifiedType mcType) {
    Preconditions.checkNotNull(mcType);
    Preconditions.checkNotNull(mcType.getEnclosingScope());
    Preconditions.checkArgument(mcType.getEnclosingScope() instanceof IVariableArcScope);

    String compTypeName = mcType.getMCQualifiedName().getQName();
    IVariableArcScope enclScope = ((IVariableArcScope) mcType.getEnclosingScope());
    List<ComponentTypeSymbol> compType = enclScope.resolveComponentTypeMany(compTypeName);

    if (compType.isEmpty()) {
      resultWrapper.setResultAbsent();
    } else {
      resultWrapper.setResult(new TypeExprOfVariableComponent(compType.get(0)));
    }
  }
}
