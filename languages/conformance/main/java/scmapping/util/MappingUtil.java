/* (c) https://github.com/MontiCore/monticore */
package scmapping.util;

import arcbasis._ast.ASTArcComponentType;
import de.monticore.ast.ASTNode;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumTOP;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolTOP;
import de.se_rwth.commons.SourcePosition;
import montiarc._prettyprint.MontiArcFullPrettyPrinter;
import montiarc.conformance.util.AutomataUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MappingUtil {
  public static List<String> getEnumConstants(ASTCDCompilationUnit cd, String enumName) {
    Optional<ASTCDEnum> astcdEnum =
        cd.getCDDefinition().getCDEnumsList().stream()
            .filter(e -> e.getName().equals(enumName))
            .findAny();

    return astcdEnum
        .map(
            anEnum ->
                anEnum.getCDEnumConstantList().stream()
                    .map(e -> enumName + "." + e.getName())
                    .collect(Collectors.toList()))
        .orElseGet(ArrayList::new);
  }

  public static List<String> getInputTypes(ASTArcComponentType aut, ASTCDCompilationUnit cd) {
    Set<String> enumsNames =
        cd.getCDDefinition().getCDEnumsList().stream()
            .map(ASTCDEnumTOP::getName)
            .collect(Collectors.toSet());
    return AutomataUtils.getInPorts(aut).stream()
        .map(p -> p.getType().getTypeInfo().getName())
        .filter(enumsNames::contains)
        .collect(Collectors.toList());
  }

  public static List<String> getOutputTypes(ASTArcComponentType aut, ASTCDCompilationUnit cd) {
    Set<String> enumsNames =
        cd.getCDDefinition().getCDEnumsList().stream()
            .map(ASTCDEnumTOP::getName)
            .collect(Collectors.toSet());
    return AutomataUtils.getOutPorts(aut).stream()
        .map(p -> p.getType().getTypeInfo().getName())
        .filter(enumsNames::contains)
        .collect(Collectors.toList());
  }

  public static List<String> getGlobalVarNames(ASTArcComponentType aut) {
    return AutomataUtils.getGlobalVariables(aut).stream()
        .map(VariableSymbolTOP::getName)
        .collect(Collectors.toList());
  }

  public static String printPosition(ASTNode node) {
    SourcePosition pos = node.get_SourcePositionStart();
    if (pos.getFileName().isPresent()) {
      String fileName = new File(pos.getFileName().get()).getName();
      return fileName + " <" + pos.getLine() + "," + pos.getColumn() + ">";
    }
    return "";
  }

  public static String print(ASTNode node) {
    MontiArcFullPrettyPrinter prettyPrinter = new MontiArcFullPrettyPrinter(new IndentPrinter());
    prettyPrinter.setPrintComments(false);
    return prettyPrinter.prettyprint(node);
  }
}
