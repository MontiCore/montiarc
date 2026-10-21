/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcField;
import arcbasis._cocos.ArcBasisASTArcFieldCoCo;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symboltable.ISymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;

import java.util.List;

import static montiarc.util.ArcError.PORT_REF_IN_STATIC_CONTEXT;

/**
 * Checks that component field declarations do not reference ports in any
 * configuration in which the field and the port are present, as ports are not
 * available in static context.
 */
public class NoPortInFieldDeclaration4Family extends NoReferenceInStaticContext4Family implements ArcBasisASTArcFieldCoCo {

  public NoPortInFieldDeclaration4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                         @NotNull PresenceResolver presenceResolver) {
    super(withinComponentResolver, presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcField node) {
    Preconditions.checkNotNull(node);
    this.checkReferences(node, List.of(node.getInitial()));
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
