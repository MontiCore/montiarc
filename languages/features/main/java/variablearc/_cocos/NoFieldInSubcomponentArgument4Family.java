/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._cocos.ArcBasisASTComponentInstanceCoCo;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;

import static montiarc.util.ArcError.FIELD_REF_IN_STATIC_CONTEXT;

/**
 * Checks that subcomponent arguments do not reference fields in any
 * configuration in which the subcomponent and the field are present, as fields
 * are not available in static context.
 */
public class NoFieldInSubcomponentArgument4Family extends NoReferenceInStaticContext4Family implements ArcBasisASTComponentInstanceCoCo {

  public NoFieldInSubcomponentArgument4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                              @NotNull PresenceResolver presenceResolver) {
    super(withinComponentResolver, presenceResolver);
  }

  @Override
  public void check(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    if (node.isPresentArcArguments()) {
      this.checkReferences(node, node.getArcArguments().getArcArgumentList().stream()
        .map(ASTArcArgument::getExpression)
        .toList());
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
