/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc.util;

import com.google.common.base.Preconditions;
import de.monticore.lang.sd4components._ast.ASTSDTick;
import de.monticore.lang.sdbasis._ast.ASTSDSendMessage;
import de.monticore.lang.sdbasis._ast.ASTSequenceDiagram;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.stream.Stream;

public class SDInteractions {

  /**
   * @return all send messages of the diagram in order, including those nested in tick blocks
   */
  public static Stream<ASTSDSendMessage> streamSendMessages(@NotNull ASTSequenceDiagram diagram) {
    Preconditions.checkNotNull(diagram);
    return diagram.getSDBody().streamSDElements()
      .flatMap(e -> e instanceof ASTSDTick tick ? tick.streamSDSendMessages()
        : e instanceof ASTSDSendMessage msg ? Stream.of(msg) : Stream.empty());
  }
}
