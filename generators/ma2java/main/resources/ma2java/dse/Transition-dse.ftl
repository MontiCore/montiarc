<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("state", "automaton", "output", "result", "counter")}


<#if ast.getSCTBody().isPresentPre()>
  {
    BoolExpr ifStatement = ${compHelperDse.printExpression(ast.getSCTBody().getPre())};
    if(montiarc.rte.dse.TestController.getIf(ifStatement,${compHelperDseValue.printExpression(ast.getSCTBody().getPre())}, "from${state.getName()}To${ast.targetName}${counter}")) {
      possibleTransitions.add(ImmutablePair.of(this::from${state.getName()}To${ast.targetName}${counter}, instanceName + "from${state.getName()}To${ast.targetName}${counter}"));
    }
  }
  <#else>
  {
    BoolExpr ifStatement = ctx.mkBool(true);
    if(montiarc.rte.dse.TestController.getIf(ifStatement,true, "from${state.getName()}To${ast.targetName}NoGuard${counter}")) {
      possibleTransitions.add(ImmutablePair.of(this::from${state.getName()}To${ast.targetName}NoGuard${counter}, instanceName + "from${state.getName()}To${ast.targetName}NoGuard${counter}"));
    }
  }
</#if>
