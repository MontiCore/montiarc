/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcautomaton._visitor.NoOtherInputPortInEventContextVisitor;
import arcautomaton._cocos.NoOtherInputPortInMsgTransition;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariantAwareNoOtherInputPortInEventContextVisitor;

public class VariantAwareNoOtherInputPortInMsgTransition extends NoOtherInputPortInMsgTransition {

  @Override
  protected NoOtherInputPortInEventContextVisitor createVisitor(@NotNull String event) {
    Preconditions.checkNotNull(event);
    Preconditions.checkArgument(!event.isBlank());
    return new VariantAwareNoOtherInputPortInEventContextVisitor(event, context);
  }
}
