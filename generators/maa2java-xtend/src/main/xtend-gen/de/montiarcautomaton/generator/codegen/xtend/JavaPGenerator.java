/**
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.montiarcautomaton.generator.codegen.xtend;

import de.montiarcautomaton.generator.codegen.xtend.BehaviorGenerator;
import de.montiarcautomaton.generator.helper.ComponentHelper;
import de.monticore.ast.ASTNode;
import de.monticore.java.javadsl._ast.ASTBlockStatement;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._ast.ASTJavaPInitializer;
import montiarc._ast.ASTValueInitialization;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class JavaPGenerator extends BehaviorGenerator {
  @Override
  public String hook(final ComponentSymbol comp) {
    StringConcatenation _builder = new StringConcatenation();
    return _builder.toString();
  }
  
  @Override
  public String generateCompute(final ComponentSymbol comp) {
    ComponentHelper helper = new ComponentHelper(comp);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("public ");
    String _name = comp.getName();
    _builder.append(_name);
    _builder.append("Result");
    _builder.newLineIfNotEmpty();
    {
      boolean _isGeneric = helper.isGeneric();
      if (_isGeneric) {
        {
          List<String> _genericParameters = helper.getGenericParameters();
          boolean _hasElements = false;
          for(final String generic : _genericParameters) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(",", "          ");
            }
            _builder.append("          ");
            _builder.append(generic, "          ");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    _builder.append("          ");
    _builder.append("compute(");
    String _name_1 = comp.getName();
    _builder.append(_name_1, "          ");
    _builder.append("Input");
    _builder.newLineIfNotEmpty();
    {
      boolean _isGeneric_1 = helper.isGeneric();
      if (_isGeneric_1) {
        {
          List<String> _genericParameters_1 = helper.getGenericParameters();
          boolean _hasElements_1 = false;
          for(final String generic_1 : _genericParameters_1) {
            if (!_hasElements_1) {
              _hasElements_1 = true;
            } else {
              _builder.appendImmediate(",", "          ");
            }
            _builder.append("          ");
            _builder.append(generic_1, "          ");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append("                ");
      }
    }
    _builder.append(" ");
    String _inputName = helper.getInputName();
    _builder.append(_inputName, "          ");
    _builder.append(") {");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("// inputs");
    _builder.newLine();
    {
      Collection<PortSymbol> _incomingPorts = comp.getIncomingPorts();
      for(final PortSymbol portIn : _incomingPorts) {
        _builder.append("  ");
        _builder.append("final ");
        String _printPortType = helper.printPortType(portIn);
        _builder.append(_printPortType, "  ");
        _builder.append(" ");
        String _name_2 = portIn.getName();
        _builder.append(_name_2, "  ");
        _builder.append(" = ");
        String _inputName_1 = helper.getInputName();
        _builder.append(_inputName_1, "  ");
        _builder.append(".get");
        String _firstUpper = StringExtensions.toFirstUpper(portIn.getName());
        _builder.append(_firstUpper, "  ");
        _builder.append("();");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.newLine();
    _builder.append("  ");
    _builder.append("final ");
    String _name_3 = comp.getName();
    _builder.append(_name_3, "  ");
    _builder.append("Result ");
    String _resultName = helper.getResultName();
    _builder.append(_resultName, "  ");
    _builder.append(" = new ");
    String _name_4 = comp.getName();
    _builder.append(_name_4, "  ");
    _builder.append("Result();");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.newLine();
    {
      Collection<PortSymbol> _outgoingPorts = comp.getOutgoingPorts();
      for(final PortSymbol portOut : _outgoingPorts) {
        _builder.append("  ");
        String _printPortType_1 = helper.printPortType(portOut);
        _builder.append(_printPortType_1, "  ");
        _builder.append(" ");
        String _name_5 = portOut.getName();
        _builder.append(_name_5, "  ");
        _builder.append(" = ");
        String _resultName_1 = helper.getResultName();
        _builder.append(_resultName_1, "  ");
        _builder.append(".get");
        String _firstUpper_1 = StringExtensions.toFirstUpper(portOut.getName());
        _builder.append(_firstUpper_1, "  ");
        _builder.append("();");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.newLine();
    _builder.append("          ");
    String _javaP = this.getJavaP(comp);
    _builder.append(_javaP, "          ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.newLine();
    {
      Collection<PortSymbol> _outgoingPorts_1 = comp.getOutgoingPorts();
      for(final PortSymbol portOut_1 : _outgoingPorts_1) {
        String _resultName_2 = helper.getResultName();
        _builder.append(_resultName_2);
        _builder.append(".set");
        String _firstUpper_2 = StringExtensions.toFirstUpper(portOut_1.getName());
        _builder.append(_firstUpper_2);
        _builder.append("(");
        String _name_6 = portOut_1.getName();
        _builder.append(_name_6);
        _builder.append(");");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.append("return ");
    String _resultName_3 = helper.getResultName();
    _builder.append(_resultName_3, "  ");
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    return _builder.toString();
  }
  
  public String getJavaP(final ComponentSymbol comp) {
    Optional<ASTJavaPBehavior> behaviorEmbedding = this.getBehaviorEmbedding(comp);
    IndentPrinter _indentPrinter = new IndentPrinter();
    JavaDSLPrettyPrinter prettyPrinter = new JavaDSLPrettyPrinter(_indentPrinter);
    StringBuilder sb = new StringBuilder();
    List<ASTBlockStatement> _blockStatementList = behaviorEmbedding.get().getBlockStatementList();
    for (final ASTBlockStatement s : _blockStatementList) {
      sb.append(prettyPrinter.prettyprint(s));
    }
    return sb.toString();
  }
  
  public Optional<ASTJavaPBehavior> getBehaviorEmbedding(final ComponentSymbol comp) {
    ASTNode _get = comp.getAstNode().get();
    ASTComponent compAST = ((ASTComponent) _get);
    List<ASTElement> elements = compAST.getBody().getElementList();
    for (final ASTElement e : elements) {
      if ((e instanceof ASTJavaPBehavior)) {
        return Optional.<ASTJavaPBehavior>of(((ASTJavaPBehavior) e));
      }
    }
    return Optional.<ASTJavaPBehavior>empty();
  }
  
  @Override
  public String generateGetInitialValues(final ComponentSymbol comp) {
    ComponentHelper helper = new ComponentHelper(comp);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("@Override");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("public ");
    String _name = comp.getName();
    _builder.append(_name, " ");
    _builder.append("Result");
    _builder.newLineIfNotEmpty();
    {
      boolean _isGeneric = helper.isGeneric();
      if (_isGeneric) {
        {
          List<String> _genericParameters = helper.getGenericParameters();
          boolean _hasElements = false;
          for(final String generic : _genericParameters) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(",", "       ");
            }
            _builder.append("       ");
            _builder.append(generic, "       ");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append("             ");
      }
    }
    _builder.append(" getInitialValues() {");
    _builder.newLineIfNotEmpty();
    _builder.append("   ");
    _builder.append("final ");
    String _name_1 = comp.getName();
    _builder.append(_name_1, "   ");
    _builder.append("Result ");
    String _resultName = helper.getResultName();
    _builder.append(_resultName, "   ");
    _builder.append(" = new ");
    String _name_2 = comp.getName();
    _builder.append(_name_2, "   ");
    _builder.append("Result();");
    _builder.newLineIfNotEmpty();
    _builder.append("   ");
    _builder.newLine();
    _builder.append("   ");
    _builder.append("try {");
    _builder.newLine();
    {
      Collection<PortSymbol> _outgoingPorts = comp.getOutgoingPorts();
      for(final PortSymbol portOut : _outgoingPorts) {
        _builder.append("   ");
        String _printPortType = helper.printPortType(portOut);
        _builder.append(_printPortType, "   ");
        _builder.append(" ");
        String _name_3 = portOut.getName();
        _builder.append(_name_3, "   ");
        _builder.append(" = null;");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("   ");
    _builder.newLine();
    {
      List<ASTValueInitialization> _initializations = this.getInitializations(comp);
      for(final ASTValueInitialization init : _initializations) {
        _builder.append("   ");
        String _printInit = ComponentHelper.printInit(init);
        _builder.append(_printInit, "   ");
        _builder.append("    ");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.newLine();
    {
      Collection<PortSymbol> _outgoingPorts_1 = comp.getOutgoingPorts();
      for(final PortSymbol portOut_1 : _outgoingPorts_1) {
        _builder.append("   ");
        String _resultName_1 = helper.getResultName();
        _builder.append(_resultName_1, "   ");
        _builder.append(".set");
        String _firstUpper = StringExtensions.toFirstUpper(portOut_1.getName());
        _builder.append(_firstUpper, "   ");
        _builder.append("(");
        String _name_4 = portOut_1.getName();
        _builder.append(_name_4, "   ");
        _builder.append(");");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("   ");
    _builder.append("} catch(Exception e) {");
    _builder.newLine();
    _builder.append("     ");
    _builder.append("e.printStackTrace();");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("return result;");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public List<ASTValueInitialization> getInitializations(final ComponentSymbol comp) {
    Optional<ASTJavaPInitializer> initialize = ComponentHelper.getComponentInitialization(comp);
    boolean _isPresent = initialize.isPresent();
    if (_isPresent) {
      return initialize.get().getValueInitializationList();
    }
    return Collections.EMPTY_LIST;
  }
}
