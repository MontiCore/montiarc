/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.HashSet;
import java.util.Set;

import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTPort;
import montiarc._ast.ASTVariableDeclaration;
import montiarc._cocos.MontiArcASTPortCoCo;
import montiarc._cocos.MontiArcASTVariableDeclarationCoCo;

/**
 * Checks that the identifiers of ports are not Java keywords.
 *
 * @implements No literature reference
 */
public class PortNamesAreNotJavaKeywords implements MontiArcASTPortCoCo{
  
  private static Set<String> KEYWORDS = new HashSet<String>() {
    {
      
      add("abstract");
      add("continue");
      add("for");
      add("new");
      add("switch");
      add("assert");
      add("default");
      add("goto");
      add("package");
      add("synchronized");
      add("boolean");
      add("do");
      add("if");
      add("private");
      add("this");
      add("break");
      add("double");
      add("implements");
      add("protected");
      add("throw");
      add("byte");
      add("else");
      add("import");
      add("public");
      add("throws");
      add("case");
      add("enum");
      add("instanceof");
      add("return");
      add("transient");
      add("catch");
      add("extends");
      add("int");
      add("short");
      add("try");
      add("char");
      add("final");
      add("interface");
      add("static");
      add("void");
      add("class");
      add("finally");
      add("long");
      add("strictfp");
      add("volatile");
      add("const");
      add("float");
      add("native");
      add("super");
      add("while");
    }
  };
  
  
  
  /**
   * @see montiarc._cocos.MontiArcASTPortCoCo#check(montiarc._ast.ASTPort)
   */
  @Override
  public void check(ASTPort node) {
    node.getNameList().forEach(s-> {
      if (isPrimitveTypeName(s)) {
        Log.error("0xMA028 The Port name must not be a keyword, but was "+s, node.get_SourcePositionStart());
      }
    });
  }
  
  private boolean isPrimitveTypeName(String datatype) {
    return KEYWORDS.contains(datatype);
  }

  
}
