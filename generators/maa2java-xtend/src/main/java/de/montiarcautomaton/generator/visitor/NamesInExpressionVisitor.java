/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.visitor;

import java.util.HashSet;
import java.util.Set;

import de.monticore.java.javadsl._visitor.JavaDSLDelegatorVisitor;
import de.monticore.java.javadsl._visitor.JavaDSLVisitor;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.mcexpressions._visitor.MCExpressionsVisitor;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class NamesInExpressionVisitor extends JavaDSLDelegatorVisitor {
  
  private NamesInExpressionVisitor realThis = this;

  
  private Set<String> foundNames = new HashSet<>();
  
  /**
   * @return foundNames
   */
  public Set<String> getFoundNames() {
    return this.foundNames;
  }
  
  /**
   * Constructor for montiarc.visitor.MyJavaDSLDelegatorVisitor
   */
  public NamesInExpressionVisitor() {
    NamesInMCExpressionsVisitor mcvisitor = new NamesInMCExpressionsVisitor();
    mcvisitor.setRealThis(getRealThis());
    NamesInJavaExpressionsVisitor javavisitor = new NamesInJavaExpressionsVisitor();
    javavisitor.setRealThis(getRealThis());
    setJavaDSLVisitor(javavisitor);
    setMCExpressionsVisitor(mcvisitor);
  }
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLDelegatorVisitor#getRealThis()
   */
  @Override
  public NamesInExpressionVisitor getRealThis() {
    return realThis;
  }
  
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLDelegatorVisitor#visit(de.monticore.mcexpressions._ast.ASTNameExpression)
   */
  @Override
  public void visit(ASTNameExpression node) {
    if(!foundNames.contains(node.getName())){
      foundNames.add(node.getName());
    }
  }
  
  
  /**
   * Visits {@link ASTExpression} and stores used names {@link ASTNameExpression}
   * and the kind of expression they are used in.
   *
   * @author Pfeiffer
   * @version $Revision$, $Date$
   */
  private class NamesInMCExpressionsVisitor implements MCExpressionsVisitor {
    
    MCExpressionsVisitor realThis = this;
    
    
    /**
     * @see de.monticore.mcexpressions._visitor.MCExpressionsVisitor#setRealThis(de.monticore.mcexpressions._visitor.MCExpressionsVisitor)
     */
    @Override
    public void setRealThis(MCExpressionsVisitor realThis) {
      this.realThis = realThis;
    }
    
    /**
     * @see de.monticore.mcexpressions._visitor.MCExpressionsVisitor#getRealThis()
     */
    @Override
    public MCExpressionsVisitor getRealThis() {
      return this.realThis;
    };
    
  }
  
  /**
   * Used for checking names in AJava blocks which uses the JavaDSL.
   *
   * @author (last commit) $Author$
   * @version $Revision$, $Date$
   */
  private class NamesInJavaExpressionsVisitor implements JavaDSLVisitor {
    
    private JavaDSLVisitor realThis = this;
    
    /**
     * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#setRealThis(de.monticore.java.javadsl._visitor.JavaDSLVisitor)
     */
    @Override
    public void setRealThis(JavaDSLVisitor realThis) {
      this.realThis = realThis;
    }
    
    /**
     * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#getRealThis()
     */
    @Override
    public JavaDSLVisitor getRealThis() {
      return this.realThis;
    }
      
  }

}
