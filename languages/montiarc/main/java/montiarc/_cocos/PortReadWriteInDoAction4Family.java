/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.PortReadWriteHandler4AssignmentExpressions;
import arcbasis._cocos.PortReadWriteHandler4CommonExpressions;
import arcbasis._cocos.PortReadWriteHandler4MCCommonStatements;
import arcbasis.context.WithinComponentResolver;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.PortReadWriteHandler4ExpressionsBasis4Family;
import variablearc.presence.PresenceResolver;

import static arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis.ContextState;

/**
 * Checks that do actions do not read from outgoing ports or write to
 * incoming ports in any configuration in which the access and the accessed
 * port are present.
 */
public class PortReadWriteInDoAction4Family extends PortReadWriteInDoAction4MontiArc {

  public PortReadWriteInDoAction4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                        @NotNull PresenceResolver presenceResolver) {
    this(MontiArcMill.traverser(), new ContextState(), withinComponentResolver, presenceResolver);
  }

  protected PortReadWriteInDoAction4Family(@NotNull MontiArcTraverser t,
                                           @NotNull ContextState c,
                                           @NotNull WithinComponentResolver withinComponentResolver,
                                           @NotNull PresenceResolver presenceResolver) {
    super(t,
      new PortReadWriteHandler4ExpressionsBasis4Family(c, withinComponentResolver, presenceResolver),
      new PortReadWriteHandler4AssignmentExpressions(c),
      new PortReadWriteHandler4CommonExpressions(c),
      new PortReadWriteHandler4MCCommonStatements(c)
    );
  }
}
