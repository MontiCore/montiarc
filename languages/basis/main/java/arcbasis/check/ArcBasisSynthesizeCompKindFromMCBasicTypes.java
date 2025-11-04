/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import de.monticore.types.check.CompKindCheckResult;
import de.monticore.types.check.SynthesizeCompKindFromMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.se_rwth.commons.logging.Log;
import org.jspecify.annotations.NonNull;

public class ArcBasisSynthesizeCompKindFromMCBasicTypes extends SynthesizeCompKindFromMCBasicTypes {

  public ArcBasisSynthesizeCompKindFromMCBasicTypes(@NonNull CompKindCheckResult resultWrapper) {
    super(resultWrapper);
  }

  @Override
  public void handle(ASTMCPrimitiveType node) {
    Log.error(String.format("0xD0104 Cannot resolve component '%s'", node.printType()),
      node.get_SourcePositionStart(), node.get_SourcePositionEnd()
    );
  }

  @Override
  public void handle(ASTMCVoidType node) {
    Log.error("0xD0104 Cannot resolve component 'void'",
      node.get_SourcePositionStart(), node.get_SourcePositionEnd()
    );
  }
}
