/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.SynthCompTypeResult;
import arcbasis.check.SynthesizeComponentFromMCBasicTypes;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcScope;

import java.util.List;

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
      Log.error(ArcError.SYMBOL_NOT_FOUND.format(compTypeName), mcType.get_SourcePositionStart());
      resultWrapper.setCurrentResultAbsent();
    }
    else {
      if (compType.size() > 1) {
        Log.error(ArcError.SYMBOL_TOO_MANY_FOUND.format(compTypeName), mcType.get_SourcePositionStart());
      }
      resultWrapper.setCurrentResult(new TypeExprOfVariableComponent(compType.get(0)));
    }
  }
}
