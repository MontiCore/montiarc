/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.*;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import variablearc._ast.ASTArcBlock;
import variablearc._ast.ASTArcIfStatement;

/**
 * {@link ASTArcElement}'s used in if-statement blocks should be supported by
 * variability (i.e. correctly added to variation points). If they are
 * unsupported unexpected behavior has to be expected.
 */
public class VariableElementsUsage implements VariableArcASTArcIfStatementCoCo, VariableArcASTArcBlockCoCo, ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(ASTComponentType node) {
    Preconditions.checkNotNull(node);

    node.getBody().getArcElementList().stream()
      .filter(e -> e instanceof ASTArcBlock).forEach(arcBlock ->
        Log.error(VariableArcError.ILLEGAL_USE.format("ArcBlock", node.getName(), "Component definition"), arcBlock.get_SourcePositionStart(),
          arcBlock.get_SourcePositionEnd())
      );
  }

  @Override
  public void check(ASTArcBlock node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope(), "Could not perform coco check '%s'. Perhaps you missed the " + "symbol table creation.",
      this.getClass().getSimpleName());

    node.getArcElementList().stream().filter(
        e -> !(e instanceof ASTArcIfStatement || e instanceof ASTComponentInstantiation || e instanceof ASTComponentInterface || e instanceof ASTConnector))
      .forEach(astElement -> {
        Log.error(VariableArcError.ILLEGAL_USE.format(astElement.getClass()
            .getSimpleName(), node.getEnclosingScope().getName(), "ArcBlock"),
          astElement.get_SourcePositionStart(), astElement.get_SourcePositionEnd());
      });
  }

  @Override
  public void check(ASTArcIfStatement node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope(), "Could not perform coco check '%s'. Perhaps you missed the " + "symbol table creation.",
      this.getClass().getSimpleName());

    ASTArcElement astElement = node.getThenStatement();
    if (!(astElement instanceof ASTArcIfStatement || astElement instanceof ASTComponentInstantiation || astElement instanceof ASTComponentInterface
      || astElement instanceof ASTConnector || astElement instanceof ASTArcBlock)) {
      Log.error(VariableArcError.ILLEGAL_USE.format(astElement.getClass()
          .getSimpleName(), node.getEnclosingScope().getName(), "If statement"),
        astElement.get_SourcePositionStart(), astElement.get_SourcePositionEnd());
    }
  }
}
