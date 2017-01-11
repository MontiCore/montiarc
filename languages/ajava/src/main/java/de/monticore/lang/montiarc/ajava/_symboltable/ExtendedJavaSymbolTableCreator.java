/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.ajava._symboltable;

import java.util.Deque;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.javadsl._ast.ASTPrimaryExpression;
import de.monticore.java.symboltable.JavaSymbolTableCreator;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class ExtendedJavaSymbolTableCreator extends JavaSymbolTableCreator{

  /**
   * Constructor for de.monticore.lang.montiarc.ajava._symboltable.ExtendedJavaSymbolTableCreator
   * @param resolvingConfig
   * @param scopeStack
   */
  public ExtendedJavaSymbolTableCreator(
      ResolvingConfiguration resolvingConfig,
      Deque<MutableScope> scopeStack) {
    super(resolvingConfig, scopeStack);
  }
  
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#visit(de.monticore.java.javadsl._ast.ASTExpression)
   */
  @Override
  public void visit(ASTExpression node) {
    super.visit(node);
  }
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#visit(de.monticore.java.javadsl._ast.ASTPrimaryExpression)
   */
  @Override
  public void visit(ASTPrimaryExpression node) {
    super.visit(node);
    if(node.getName().isPresent()){
      SimpleVariableSymbol variable = new SimpleVariableSymbol(node.getName().get());
      addToScopeAndLinkWithNode(variable, node);
    }
  }

}
