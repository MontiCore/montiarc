/**
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.montiarcautomaton.generator.codegen.xtend;

import de.montiarcautomaton.generator.codegen.xtend.BehaviorGenerator;
import de.montiarcautomaton.generator.helper.AutomatonHelper;
import de.montiarcautomaton.generator.helper.ComponentHelper;
import de.montiarcautomaton.generator.helper.IOAssignmentHelper;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.Symbol;
import java.util.Collection;
import java.util.List;
import montiarc._ast.ASTAutomaton;
import montiarc._ast.ASTAutomatonBehavior;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTState;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.StateSymbol;
import montiarc._symboltable.TransitionSymbol;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class AutomatonGenerator extends BehaviorGenerator {
  @Override
  public String hook(final ComponentSymbol comp) {
    ComponentHelper compHelper = new ComponentHelper(comp);
    ASTAutomaton automaton = null;
    ASTNode _get = comp.getAstNode().get();
    List<ASTElement> _elementList = ((ASTComponent) _get).getBody().getElementList();
    for (final ASTElement element : _elementList) {
      if ((element instanceof ASTAutomatonBehavior)) {
        automaton = ((ASTAutomatonBehavior)element).getAutomaton();
      }
    }
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("private ");
    String _name = comp.getName();
    _builder.append(_name);
    _builder.append("State ");
    String _currentStateName = compHelper.getCurrentStateName();
    _builder.append(_currentStateName);
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.newLine();
    _builder.append("private static enum ");
    String _name_1 = comp.getName();
    _builder.append(_name_1);
    _builder.append("State {");
    _builder.newLineIfNotEmpty();
    {
      List<ASTState> _stateList = automaton.getStateDeclaration(0).getStateList();
      boolean _hasElements = false;
      for(final ASTState state : _stateList) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate(",", "  ");
        }
        _builder.append("  ");
        String _name_2 = state.getName();
        _builder.append(_name_2, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
      }
    }
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  @Override
  public String generateCompute(final ComponentSymbol comp) {
    String _name = comp.getName();
    String resultName = (_name + "Result");
    ASTAutomaton automaton = null;
    AutomatonHelper helper = new AutomatonHelper(comp);
    ComponentHelper compHelper = new ComponentHelper(comp);
    ASTNode _get = comp.getAstNode().get();
    List<ASTElement> _elementList = ((ASTComponent) _get).getBody().getElementList();
    for (final ASTElement element : _elementList) {
      if ((element instanceof ASTAutomatonBehavior)) {
        automaton = ((ASTAutomatonBehavior)element).getAutomaton();
      }
    }
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("public ");
    String _name_1 = comp.getName();
    _builder.append(_name_1);
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
              _builder.appendImmediate(",", "      ");
            }
            _builder.append("      ");
            _builder.append(generic, "      ");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    _builder.append("      ");
    _builder.append("compute(");
    String _name_2 = comp.getName();
    _builder.append(_name_2, "      ");
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
              _builder.appendImmediate(",", "      ");
            }
            _builder.append("      ");
            _builder.append(generic_1, "      ");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append("          ");
      }
    }
    _builder.append(" ");
    String _inputName = helper.getInputName();
    _builder.append(_inputName, "      ");
    _builder.append(") {");
    _builder.newLineIfNotEmpty();
    _builder.append("    ");
    _builder.append("// inputs");
    _builder.newLine();
    {
      Collection<PortSymbol> _incomingPorts = comp.getIncomingPorts();
      for(final PortSymbol inPort : _incomingPorts) {
        _builder.append("    ");
        _builder.append("final ");
        String _printPortType = helper.printPortType(inPort);
        _builder.append(_printPortType, "    ");
        _builder.append(" ");
        String _name_3 = inPort.getName();
        _builder.append(_name_3, "    ");
        _builder.append(" = ");
        String _inputName_1 = helper.getInputName();
        _builder.append(_inputName_1, "    ");
        _builder.append(".get");
        String _firstUpper = StringExtensions.toFirstUpper(inPort.getName());
        _builder.append(_firstUpper, "    ");
        _builder.append("();");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("    ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("final ");
    _builder.append(resultName, "    ");
    _builder.append(" ");
    String _resultName = helper.getResultName();
    _builder.append(_resultName, "    ");
    _builder.append(" = new ");
    _builder.append(resultName, "    ");
    _builder.append("();");
    _builder.newLineIfNotEmpty();
    _builder.append("    ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("// first current state to reduce stimuli and guard checks");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("switch (");
    String _currentStateName = compHelper.getCurrentStateName();
    _builder.append(_currentStateName, "    ");
    _builder.append(") {");
    _builder.newLineIfNotEmpty();
    {
      List<ASTState> _stateList = automaton.getStateDeclarationList().get(0).getStateList();
      for(final ASTState state : _stateList) {
        _builder.append("    ");
        _builder.append("case ");
        String _name_4 = state.getName();
        _builder.append(_name_4, "    ");
        _builder.append(":");
        _builder.newLineIfNotEmpty();
        {
          Symbol _get_1 = state.getSymbolOpt().get();
          Collection<TransitionSymbol> _transitions = helper.getTransitions(((StateSymbol) _get_1));
          for(final TransitionSymbol transition : _transitions) {
            _builder.append("    ");
            _builder.append("  ");
            _builder.append("// transition: ");
            String _string = transition.toString();
            _builder.append(_string, "      ");
            _builder.newLineIfNotEmpty();
            _builder.append("    ");
            _builder.append("  ");
            _builder.append("if (");
            {
              boolean _isPresent = transition.getGuardAST().isPresent();
              if (_isPresent) {
                String _guard = helper.getGuard(transition);
                _builder.append(_guard, "      ");
              } else {
                _builder.append(" true ");
              }
            }
            _builder.append(") {");
            _builder.newLineIfNotEmpty();
            _builder.append("    ");
            _builder.append("  ");
            _builder.append("  ");
            _builder.append("//reaction");
            _builder.newLine();
            {
              Collection<IOAssignmentHelper> _reaction = helper.getReaction(transition);
              for(final IOAssignmentHelper assignment : _reaction) {
                {
                  boolean _isAssignment = assignment.isAssignment();
                  if (_isAssignment) {
                    {
                      boolean _isVariable = assignment.isVariable(assignment.getLeft());
                      if (_isVariable) {
                        _builder.append("    ");
                        _builder.append("  ");
                        _builder.append("  ");
                        String _left = assignment.getLeft();
                        _builder.append(_left, "        ");
                        _builder.append(" = ");
                        String _right = assignment.getRight();
                        _builder.append(_right, "        ");
                        _builder.append(";");
                        _builder.newLineIfNotEmpty();
                      } else {
                        _builder.append("    ");
                        _builder.append("  ");
                        _builder.append("  ");
                        String _resultName_1 = helper.getResultName();
                        _builder.append(_resultName_1, "        ");
                        _builder.append(".set");
                        String _firstUpper_1 = StringExtensions.toFirstUpper(assignment.getLeft());
                        _builder.append(_firstUpper_1, "        ");
                        _builder.append("(");
                        String _right_1 = assignment.getRight();
                        _builder.append(_right_1, "        ");
                        _builder.append(");");
                        _builder.newLineIfNotEmpty();
                      }
                    }
                  } else {
                    _builder.append("    ");
                    _builder.append("  ");
                    _builder.append("  ");
                    String _right_2 = assignment.getRight();
                    _builder.append(_right_2, "        ");
                    _builder.append(";  ");
                    _builder.newLineIfNotEmpty();
                  }
                }
              }
            }
            _builder.append("    ");
            _builder.append("  ");
            _builder.append("  ");
            _builder.newLine();
            _builder.append("    ");
            _builder.append("  ");
            _builder.append("  ");
            _builder.append("//state change");
            _builder.newLine();
            _builder.append("    ");
            _builder.append("  ");
            _builder.append("  ");
            String _currentStateName_1 = compHelper.getCurrentStateName();
            _builder.append(_currentStateName_1, "        ");
            _builder.append(" = ");
            String _name_5 = comp.getName();
            _builder.append(_name_5, "        ");
            _builder.append("State.");
            String _name_6 = transition.getTarget().getName();
            _builder.append(_name_6, "        ");
            _builder.append(";");
            _builder.newLineIfNotEmpty();
            _builder.append("    ");
            _builder.append("  ");
            _builder.append("  ");
            _builder.append("break;");
            _builder.newLine();
            _builder.append("    ");
            _builder.append("  ");
            _builder.append("}");
            _builder.newLine();
            _builder.append("    ");
            _builder.append("  ");
            _builder.newLine();
            _builder.append("    ");
            _builder.append("  ");
            _builder.newLine();
          }
        }
      }
    }
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("return result;");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  @Override
  public String generateGetInitialValues(final ComponentSymbol comp) {
    String _name = comp.getName();
    String resultName = (_name + "Result");
    ASTAutomaton automaton = null;
    ComponentHelper compHelper = new ComponentHelper(comp);
    AutomatonHelper helper = new AutomatonHelper(comp);
    ASTNode _get = comp.getAstNode().get();
    List<ASTElement> _elementList = ((ASTComponent) _get).getBody().getElementList();
    for (final ASTElement element : _elementList) {
      if ((element instanceof ASTAutomatonBehavior)) {
        automaton = ((ASTAutomatonBehavior)element).getAutomaton();
      }
    }
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("public ");
    _builder.append(resultName);
    _builder.append(" ");
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
              _builder.appendImmediate(",", "      ");
            }
            _builder.append("      ");
            _builder.append(generic, "      ");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    _builder.append("getInitialValues() {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("final ");
    _builder.append(resultName, "  ");
    _builder.append(" ");
    String _resultName = helper.getResultName();
    _builder.append(_resultName, "  ");
    _builder.append(" = new ");
    _builder.append(resultName, "  ");
    _builder.append("();");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("    ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("// initial reaction");
    _builder.newLine();
    {
      Collection<IOAssignmentHelper> _initialReaction = helper.getInitialReaction(helper.getInitialState());
      for(final IOAssignmentHelper assignment : _initialReaction) {
        {
          boolean _isAssignment = assignment.isAssignment();
          if (_isAssignment) {
            {
              boolean _isPort = helper.isPort(assignment.getLeft());
              if (_isPort) {
                _builder.append("  ");
                String _resultName_1 = helper.getResultName();
                _builder.append(_resultName_1, "  ");
                _builder.append(".set");
                String _firstUpper = StringExtensions.toFirstUpper(assignment.getLeft());
                _builder.append(_firstUpper, "  ");
                _builder.append("(");
                String _right = assignment.getRight();
                _builder.append(_right, "  ");
                _builder.append(");");
                _builder.newLineIfNotEmpty();
              } else {
                _builder.append("  ");
                String _left = assignment.getLeft();
                _builder.append(_left, "  ");
                _builder.append(" = ");
                String _right_1 = assignment.getRight();
                _builder.append(_right_1, "  ");
                _builder.append(";");
                _builder.newLineIfNotEmpty();
              }
            }
          }
        }
      }
    }
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    String _currentStateName = compHelper.getCurrentStateName();
    _builder.append(_currentStateName, "  ");
    _builder.append(" = ");
    String _name_1 = comp.getName();
    _builder.append(_name_1, "  ");
    _builder.append("State.");
    String _name_2 = automaton.getInitialStateDeclarationList().get(0).getName();
    _builder.append(_name_2, "  ");
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("return ");
    String _resultName_2 = helper.getResultName();
    _builder.append(_resultName_2, "  ");
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
}
