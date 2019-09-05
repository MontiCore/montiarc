/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.generator.visitor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import de.monticore.java.javadsl._visitor.JavaDSLVisitor;
import de.monticore.literals.literals._ast.ASTNullLiteral;
import de.monticore.literals.literals._visitor.LiteralsVisitor;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.mcexpressions._ast.ASTIdentityExpression;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.mcexpressions._visitor.MCExpressionsVisitor;
import montiarc._visitor.MontiArcDelegatorVisitor;

/**
 * TODO: Write me!
 *
 * @since 5.1.2
 */
public class NoDataUsageVisitor extends MontiArcDelegatorVisitor {
  
  private Set<String> portsComparedToNoData = new HashSet<>();
  
  private Optional<String> currentName = Optional.empty();
  
  private Boolean foundNoData = false;
  
  private NoDataUsageVisitor realThis = this;
  
  /**
   * Constructor for montiarc.visitor.NoDataUsageVisitor
   */
  public NoDataUsageVisitor() {
    NamesInMCExpressionsVisitor mcvisitor = new NamesInMCExpressionsVisitor();
    mcvisitor.setRealThis(getRealThis());
    NamesInJavaExpressionsVisitor javavisitor = new NamesInJavaExpressionsVisitor();
    javavisitor.setRealThis(getRealThis());
    setJavaDSLVisitor(javavisitor);
    setMontiArcVisitor(getRealThis());
    setMCExpressionsVisitor(mcvisitor);
    Lit literalsVisitor = new Lit();
    literalsVisitor.setRealThis(getRealThis());
    setLiteralsVisitor(literalsVisitor);
  }
  
  /**
   * @see montiarc._visitor.MontiArcDelegatorVisitor#visit(de.monticore.literals.literals._ast.ASTNullLiteral)
   */
  @Override
  public void visit(ASTNullLiteral node) {
    if (currentName.isPresent()) {
      portsComparedToNoData.add(currentName.get());
    }
    else {
      foundNoData = true;
    }
  }
  
  
  /**
   * @return realThis
   */
  public NoDataUsageVisitor getRealThis() {
    return this.realThis;
  }
  
  /**
   * @return portsComparedToNoData
   */
  public Set<String> getPortsComparedToNoData() {
    return this.portsComparedToNoData;
  }
  
  /**
   * @see de.monticore.mcexpressions._visitor.MCExpressionsVisitor#visit(de.monticore.mcexpressions._ast.ASTIdentityExpression)
   */
  @Override
  public void visit(ASTIdentityExpression node) {
    currentName = Optional.empty();
  }
  
  /**
   * @see de.monticore.mcexpressions._visitor.MCExpressionsVisitor#endVisit(de.monticore.mcexpressions._ast.ASTIdentityExpression)
   */
  @Override
  public void endVisit(ASTIdentityExpression node) {
    currentName = Optional.empty();
  }
  
  /**
   * @see de.monticore.mcexpressions._visitor.MCExpressionsVisitor#visit(de.monticore.mcexpressions._ast.ASTNameExpression)
   */
  @Override
  public void visit(ASTNameExpression node) {
    if (foundNoData) {
      portsComparedToNoData.add(node.getName());
    }
    else {
      currentName = Optional.of(node.getName());
    }
  }
  
  
  /**
   * Visits {@link ASTExpression} and stores used names
   * {@link ASTNameExpression} and the kind of expression they are used in.
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
    }
    
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
  
  /**
   * Used for checking names in AJava blocks which uses the JavaDSL.
   *
   * @author (last commit) $Author$
   * @version $Revision$, $Date$
   */
  private class Lit implements LiteralsVisitor {
    
    private LiteralsVisitor realThis = this;
    
    /**
     * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#setRealThis(de.monticore.java.javadsl._visitor.JavaDSLVisitor)
     */
    @Override
    public void setRealThis(LiteralsVisitor realThis) {
      this.realThis = realThis;
    }
    
    /**
     * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#getRealThis()
     */
    @Override
    public LiteralsVisitor getRealThis() {
      return this.realThis;
    }
    
  }
  
}
