<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTSequenceDiagram ast -->
  component AssertOrder__ assertOrder__ {

<#list helper.getVisibleComponents(ast) as comp>
  <#list helper.getComponentPorts(comp) as portSymbol>
    <#if portSymbol.isOutgoing() || helper.connectedToWorld(ast, comp.getName(), portSymbol)>
    port <#if portSymbol.isIncoming()>out<#else>in</#if> ${portSymbol.getType().printFullName()} port_${comp.getName()}_${portSymbol.getName()};
    </#if>
  </#list>
</#list>

<#list helper.getVisibleComponents(ast) as comp>
  <#list helper.getComponentPorts(comp) as portSymbol>
    <#if portSymbol.isOutgoing() || helper.connectedToWorld(ast, comp.getName(), portSymbol)>
    ${portSymbol.getType().printFullName()} ${comp.getName()}_${portSymbol.getName()} = ${helper.getNullLikeValue(portSymbol.getType())};
    </#if>
  </#list>
</#list>

<#list helper.getVariableDeclarations(ast) as decl>
    ${decl.getSymbol().getType().printFullName()} ${decl.getName()} = ${helper.getNullLikeValue(decl.getSymbol().getType())};
</#list>

    long countedTicks = 0;

    <<delayed>> automaton {
    <#assign stateCount = 0>
      initial {
  <#list ast.getSDBody().getSDElementList() as element>
    <#-- Trigger interaction -->
    <#if typeDispatcher.isSDBasisASTSDSendMessage(element) && element.isPresentSDTarget() && typeDispatcher.isSD4ComponentsASTSDMessage(element.getSDAction()) && element.getSDAction().isTrigger()>
        <#-- TODO in embedded send to all ports this target's source is connected to -->
        ${element.getSDTarget().getName()}_${element.getSDTarget().getPort()} = ${prettyPrinter.prettyprint(element.getSDAction().getExpression())};
        port_${element.getSDTarget().getName()}_${element.getSDTarget().getPort()} = ${element.getSDTarget().getName()}_${element.getSDTarget().getPort()};
    <#-- Observe interaction -->
    <#elseif typeDispatcher.isSDBasisASTSDSendMessage(element) && element.isPresentSDSource()>
      }<#if !(stateCount == 0)>;</#if>
      <#assign portVarName>${element.getSDSource().getName()}_${element.getSDSource().getPort()}</#assign>
      <#assign portName>port_${portVarName}</#assign>
      state S${stateCount};
      <#-- Transition to failed state if wrong message and not free -->
      <#if !typeDispatcher.isSD4ComponentsASTSDIncompleteAction(element.getSDAction()) && helper.isFree(ast, element)>
      S${stateCount} -> Failed [${portName} != (${prettyPrinter.prettyprint(element.getSDAction().getExpression())})] ${portName};
      </#if>
      <#-- Next documented transition -->
      S${stateCount} -> S${stateCount + 1} <#if !typeDispatcher.isSD4ComponentsASTSDIncompleteAction(element.getSDAction())>[${portName} == (${prettyPrinter.prettyprint(element.getSDAction().getExpression())})]</#if> ${portName} / {
      <#assign stateCount = stateCount + 1>
        ${portVarName} = ${portName};
    <#-- Assert statement -->
    <#elseif typeDispatcher.isSD4ComponentsASTSDCondition(element)>
        montiarc.maunit.api.Assertions.assertTrue(${prettyPrinter.prettyprint(element.getExpression())});
    <#-- Variable interaction -->
    <#elseif typeDispatcher.isSD4ComponentsASTSDVariableDeclaration(element)>
        ${element.getName()} = ${prettyPrinter.prettyprint(element.getAssignment())};
    </#if>
  </#list>
      }<#if !(stateCount == 0)>;</#if>
      state S${stateCount} {
        entry / {
          montiarc.lang.Simulation.stop();
        }
      };
      state Failed {
        entry / {
          montiarc.maunit.api.Assertions.fail();
        }
      };

  <#-- manual star transiton -->
  <#list 0..stateCount-1 as i>
    <#list helper.getVisibleComponents(ast) as comp>
      <#list helper.getComponentPorts(comp) as portSymbol>
        <#if portSymbol.isOutgoing()>
          <#if !helper.isPortSourceAtObserveInteractionIndex(i, ast, comp, portSymbol) && !helper.isFree(ast, comp, portSymbol)>
      S${i} -> Failed port_${comp.getName()}_${portSymbol.getName()};
          <#else>
      S${i} -> S${i} port_${comp.getName()}_${portSymbol.getName()} / {${comp.getName()}_${portSymbol.getName()} = port_${comp.getName()}_${portSymbol.getName()};};
          </#if>
        </#if>
      </#list>
    </#list>
      S${i} -> Failed [countedTicks >= ${helper.tickLength(ast)}];
      S${i} -> S${i} [countedTicks < ${helper.tickLength(ast)}] / {countedTicks = countedTicks + 1;};

  </#list>
    }
  }

<#list helper.getVisibleComponents(ast) as comp>
<#list helper.getComponentPorts(comp) as portSymbol>
  <#if portSymbol.isIncoming() && helper.connectedToWorld(ast, comp.getName(), portSymbol)>
  assertOrder__.port_${comp.getName()}_${portSymbol.getName()} -> ${comp.getName()}.${portSymbol.getName()};
  <#elseif portSymbol.isOutgoing()>
  ${comp.getName()}.${portSymbol.getName()} -> assertOrder__.port_${comp.getName()}_${portSymbol.getName()};
  </#if>
</#list>
</#list>
