/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.prettyprint;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.MCBasicTypesHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;
import montiarc.generator.ma2kotlin.codegen.TypeTrimTool;

import java.util.Iterator;

public class MCSimpleGenericTypesPrinter extends MCSimpleGenericTypesPrettyPrinter {
  public MCSimpleGenericTypesPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void handle(ASTMCBasicGenericType node) {
    super.handle(node);
    getPrinter().print("?");
  }
}
