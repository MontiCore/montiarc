/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcParent;
import arcbasis._cocos.ArcBasisASTArcParentCoCo;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symboltable.ISymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;

import static montiarc.util.ArcError.PORT_REF_IN_STATIC_CONTEXT;

/**
 * Checks that super component arguments do not reference ports in any
 * configuration in which the port is present, as ports are not available in
 * static context.
 */
public class NoPortInSuperComponentArgument4Family extends NoReferenceInStaticContext4Family implements ArcBasisASTArcParentCoCo {

  public NoPortInSuperComponentArgument4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                               @NotNull PresenceResolver presenceResolver) {
    super(withinComponentResolver, presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcParent node) {
    Preconditions.checkNotNull(node);
    this.checkReferences(node, node.getArcArgumentList().stream()
      .map(ASTArcArgument::getExpression)
      .toList());
  }

  @Override
  protected boolean isForbidden(@NotNull ISymbol member) {
    return member instanceof PortSymbol;
  }

  @Override
  protected String message(@NotNull String name) {
    return PORT_REF_IN_STATIC_CONTEXT.format(name);
  }
}
