/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._ast.ASTStateBody;
import arcautomaton._cocos.ArcAutomatonASTStateBodyCoCo;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.scactions._ast.ASTSCEntryAction;
import de.monticore.scactions._ast.ASTSCExitAction;
import de.monticore.scactions._cocos.SCActionsASTSCEntryActionCoCo;
import de.monticore.scactions._cocos.SCActionsASTSCExitActionCoCo;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._cocos.SCBasisASTSCStateCoCo;
import de.monticore.sctransitions4code._ast.ASTSCEvent;
import de.monticore.sctransitions4code._cocos.SCTransitions4CodeASTSCEventCoCo;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Contains subclasses that Inform users that they use statechart model elements that currently are not supported by
 * MontiArc.
 */
public interface UnsupportedAutomatonElements {

  /** Hierarchical states are currently not supported by MontiArc. */
  class HierarchicalStates implements ArcAutomatonASTStateBodyCoCo {

    @Override
    public void check(@NotNull ASTStateBody node) {
      Preconditions.checkNotNull(node);
      // Check if the state body contains states by its own (which is illegal as we do not support hierarchical states).
      if (node.streamSCStateElements().anyMatch(ASTSCState.class::isInstance)) {
        Log.error(ArcError.UNSUPPORTED_MODEL_ELEMENT.format("Using hierarchical states is currently not supported " +
          "by MontiArc.", node.get_SourcePositionStart(), node.get_SourcePositionEnd())
        );
      }
    }
  }

  /** Entry actions for states in automatas are currently not supported by MontiArc. */
  class EntryActions implements SCActionsASTSCEntryActionCoCo {

    @Override
    public void check(@NotNull ASTSCEntryAction node) {
      Preconditions.checkNotNull(node);
      Log.error(ArcError.UNSUPPORTED_MODEL_ELEMENT.format("Using entry actions for states in automatas is currently " +
        "not supported by MontiArc."), node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }

  /** Exit actions for states in automatas are currently not supported by MontiArc. */
  class ExitActions implements SCActionsASTSCExitActionCoCo {

    @Override
    public void check(@NotNull ASTSCExitAction node) {
      Preconditions.checkNotNull(node);
      Log.error(ArcError.UNSUPPORTED_MODEL_ELEMENT.format("Using exit actions for states in automatas is currently " +
        "not supported by MontiArc."), node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }

  /** Using trigger events for transitions in automatas is currently not supported by MontiArc. */
  class TriggerEvents implements SCTransitions4CodeASTSCEventCoCo {

    @Override
    public void check(@NotNull ASTSCEvent node) {
      Preconditions.checkNotNull(node);
      Log.error(ArcError.UNSUPPORTED_MODEL_ELEMENT.format("Using trigger events for transitions in automatas is " +
        "currently not supported by MontiArc."), node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }

  /** Using final states in automatas is not supported by MontiArc. */
  class FinalStates implements SCBasisASTSCStateCoCo {

    @Override
    public void check(@NotNull ASTSCState node) {
      Preconditions.checkNotNull(node);
      if(node.getSCModifier().isFinal()) {
        Log.error(ArcError.UNSUPPORTED_MODEL_ELEMENT.format("Using final states in automatas is not supported by " +
          "MontiArc."), node.get_SourcePositionStart(), node.get_SourcePositionEnd()
        );
      }
    }
  }

  /**
   * Catches the unsupported declaration of initial states by using the 'initial' modifier for states that is introduced
   * in SCBasis. However, we have our own way to declare initial states (InitialInputOutputDeclaration) that must be
   * used.
   */
  class WrongInitialStateDeclaration implements SCBasisASTSCStateCoCo {

    @Override
    public void check(@NotNull ASTSCState node) {
      Preconditions.checkNotNull(node);
      if(node.getSCModifier().isInitial()) {
        Log.error(ArcError.UNSUPPORTED_MODEL_ELEMENT.format("You declared your initial state wrong. You used the " +
            "unsupported 'initial' modifier in front of your state declaration:" +
            "\n    initial state " + node.getName() + ";" +
            "\n  You should declare your state separately from marking it as initial. You must also provide initial values " +
            "for outgoing ports if you have any. The declaration should look like:" +
            "\n    state " + node.getName() + ";" +
            "\n    initial " + node.getName()) + "/ { " +
            "\n      yourOutPort1 = val1;" +
            "\n      yourOutPort2 = val2;" +
            "\n      //..." +
            "\n    }",
          node.get_SourcePositionStart(), node.get_SourcePositionEnd()
        );
      }
    }
  }
}
