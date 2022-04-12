/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.prettyprint;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.MCBasicTypesHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import montiarc.generator.ma2kotlin.codegen.TypeTrimTool;

public class MCBasicTypesPrinter extends MCBasicTypesPrettyPrinter {
  public MCBasicTypesPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void handle(ASTMCPrimitiveType type) {
    Preconditions.checkNotNull(type);
    String primitive = MCBasicTypesHelper.primitiveConst2Name(type.getPrimitive());
    Preconditions.checkArgument(!Strings.isNullOrEmpty(primitive));
    // print with first char upper case
    getPrinter().print(Character.toUpperCase(primitive.charAt(0)));
    getPrinter().print(primitive.substring(1));
  }

  @Override
  public void handle(ASTMCImportStatement statement) {
    if (!TypeTrimTool.oddImports.contains(statement.getQName())) {
      super.handle(statement);
    }
  }

  @Override
  public void handle(ASTMCQualifiedName mcName) {
    if (TypeTrimTool.java2Kotlin.containsKey(mcName.getBaseName())) {
      mcName.getPartsList().forEach(part -> {
        getPrinter().print(part);
        getPrinter().print(".");
      });
      getPrinter().print(TypeTrimTool.java2Kotlin.get(mcName.getBaseName()));
    } else {
      super.handle(mcName);
    }
  }
}
