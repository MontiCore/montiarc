/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcParameter;
import arcbasis._cocos.ArcBasisASTArcParameterCoCo;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;

import java.util.List;

import static montiarc.util.ArcError.FIELD_REF_IN_STATIC_CONTEXT;

/**
 * Checks that parameter default values do not reference fields in any
 * configuration in which the field is present, as fields are not available in
 * static context.
 */
public class NoFieldInDefaultParameterValue4Family extends NoReferenceInStaticContext4Family implements ArcBasisASTArcParameterCoCo {

  public NoFieldInDefaultParameterValue4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                               @NotNull PresenceResolver presenceResolver) {
    super(withinComponentResolver, presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcParameter node) {
    Preconditions.checkNotNull(node);
    if (node.isPresentDefault()) {
      this.checkReferences(node, List.of(node.getDefault()));
    }
  }

  @Override
  protected boolean isForbidden(@NotNull ISymbol member) {
    return member instanceof VariableSymbol variable
      && variable.isPresentAstNode() && variable.getAstNode() instanceof ASTArcField;
  }

  @Override
  protected String message(@NotNull String name) {
    return FIELD_REF_IN_STATIC_CONTEXT.format(name);
  }
}
