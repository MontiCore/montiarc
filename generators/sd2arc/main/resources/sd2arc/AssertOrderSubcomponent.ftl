<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTSequenceDiagram ast -->
<#-- @ftlvariable name="helper" type="de.monticore.sd2arc.codegen.SDHelper" -->
<#-- Synchronous streams carry a message in every tick, hence the inputs are sent once per tick transition -->
<#macro sendSyncInputs>
  <#list helper.getSyncInputs(ast) as input>
        <#if input.nullable()>if (${input.varName()} != null) </#if>port_${input.varName()} = ${input.varName()};
  </#list>
</#macro>
  component AssertOrder__ assertOrder__ {

<#list helper.getVisibleComponents(ast) as comp>
  <#list helper.getComponentPorts(comp) as portSymbol>
    <#if portSymbol.isOutgoing() || helper.connectedToWorld(ast, comp.getName(), portSymbol)>
    port ${helper.printPortTiming(portSymbol)}<#if portSymbol.isIncoming()>out<#else>in</#if> ${portSymbol.getType().printFullName()} port_${comp.getName()}_${portSymbol.getName()};
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
    boolean initialized__ = false;

    <<delayed>> automaton {
    <#assign stateCount = 0>
    <#assign strictTickGuards = {}>
    <#assign restricted = helper.isRestrictedFromStart(ast)>
    <#assign inTickAction = false>
      <#-- The initial actions are executed once when entering S0 so that S0 already observes their responses -->
      initial state S0 {
        entry / {
          if (!initialized__) {
            initialized__ = true;
  <#list ast.getSDBody().getSDElementList() as element>
    <#-- Trigger interaction -->
    <#if helper.isSendMessage(element) && element.isPresentSDTarget() && helper.isMessage(element.getSDAction()) && element.getSDAction().isTrigger()>
        <#-- TODO in embedded send to all ports this target's source is connected to -->
          ${element.getSDTarget().getName()}_${element.getSDTarget().getPort()} = ${prettyPrinter.prettyprint(element.getSDAction().getExpression())};
      <#if !(inTickAction && helper.isSyncTarget(ast, element))>
          port_${element.getSDTarget().getName()}_${element.getSDTarget().getPort()} = ${element.getSDTarget().getName()}_${element.getSDTarget().getPort()};
      </#if>
    <#-- Tick interaction, triggers fire at block start -->
    <#elseif helper.isTick(element)>
      <#list helper.getTickTriggers(element) as trig>
        ${trig.getSDTarget().getName()}_${trig.getSDTarget().getPort()} = ${prettyPrinter.prettyprint(trig.getSDAction().getExpression())};
        <#if !(inTickAction && helper.isSyncTarget(ast, trig))>
        port_${trig.getSDTarget().getName()}_${trig.getSDTarget().getPort()} = ${trig.getSDTarget().getName()}_${trig.getSDTarget().getPort()};
        </#if>
      </#list>
      <#assign allConstrained = (helper.getTickMessages(element)?size > 0)>
      <#list helper.getTickMessages(element) as msg>
        <#if helper.isIncompleteAction(msg.getSDAction()) || helper.isFree(ast, msg)>
          <#assign allConstrained = false>
        </#if>
      </#list>
      <#if (stateCount == 0)>
          }
        }
      }
      <#else>
      <#if inTickAction><@sendSyncInputs/></#if>
      }
      state S${stateCount};
      </#if>
      <#-- Failed edge per non-free message in the tick block, suppressed before the first interaction -->
      <#list helper.getTickMessages(element) as msg>
        <#if (stateCount > 0 || restricted) && !helper.isIncompleteAction(msg.getSDAction()) && !helper.isFree(ast, msg)>
      S${stateCount} -> Failed [port_${msg.getSDSource().getName()}_${msg.getSDSource().getPort()} != (${prettyPrinter.prettyprint(msg.getSDAction().getExpression())})] port_${msg.getSDSource().getName()}_${msg.getSDSource().getPort()};
        </#if>
      </#list>
      <#-- AND-joined guard over complete messages -->
      <#assign guardParts = []>
      <#list helper.getTickMessages(element) as msg>
        <#if !helper.isIncompleteAction(msg.getSDAction())>
          <#assign guardParts = guardParts + ["port_${msg.getSDSource().getName()}_${msg.getSDSource().getPort()} == (${prettyPrinter.prettyprint(msg.getSDAction().getExpression())})"]>
        </#if>
      </#list>
      <#-- A tick block is strict if no deviating tick may occur in between -->
      <#if (guardParts?size > 0) && (allConstrained || helper.hasConstrainedSyncPorts(ast))>
        <#assign strictTickGuards = strictTickGuards + {stateCount?c: guardParts?join(" && ")}>
      </#if>
      S${stateCount} -> S${stateCount + 1}<#if (guardParts?size > 0)> [${guardParts?join(" && ")}]</#if> / {
      <#assign stateCount = stateCount + 1>
      <#assign inTickAction = true>
        countedTicks = countedTicks + 1;
      <#-- Track sync port values -->
      <#list helper.getVisibleComponents(ast) as comp>
        <#list helper.getComponentPorts(comp) as portSymbol>
          <#if portSymbol.isOutgoing() && helper.isSyncPort(portSymbol)>
        ${comp.getName()}_${portSymbol.getName()} = port_${comp.getName()}_${portSymbol.getName()};
          </#if>
        </#list>
      </#list>
    <#-- Observe interaction -->
    <#elseif helper.isSendMessage(element) && element.isPresentSDSource()>
      <#if (stateCount == 0)>
          }
        }
      }
      <#else>
      <#if inTickAction><@sendSyncInputs/></#if>
      }
      state S${stateCount};
      </#if>
      <#assign portVarName>${element.getSDSource().getName()}_${element.getSDSource().getPort()}</#assign>
      <#assign portName>port_${portVarName}</#assign>
      <#-- Transition to failed state if wrong message and not free, suppressed before the first interaction -->
      <#if (stateCount > 0 || restricted) && !helper.isIncompleteAction(element.getSDAction()) && !helper.isFree(ast, element)>
      S${stateCount} -> Failed [${portName} != (${prettyPrinter.prettyprint(element.getSDAction().getExpression())})] ${portName};
      </#if>
      <#-- Next documented transition -->
      S${stateCount} -> S${stateCount + 1} <#if !helper.isIncompleteAction(element.getSDAction())>[${portName} == (${prettyPrinter.prettyprint(element.getSDAction().getExpression())})]</#if> ${portName} / {
      <#assign stateCount = stateCount + 1>
      <#assign inTickAction = false>
        ${portVarName} = ${portName};
    <#-- Assert statement -->
    <#elseif helper.isCondition(element)>
        montiarc.maunit.api.Assertions.assertTrue(${prettyPrinter.prettyprint(element.getExpression())});
    <#-- Variable interaction -->
    <#elseif helper.isVariableDeclaration(element)>
        ${element.getName()} = ${prettyPrinter.prettyprint(element.getAssignment())};
    </#if>
  </#list>
      <#if (stateCount == 0)>
          }
        }
      }
      <#else>
      <#if inTickAction><@sendSyncInputs/></#if>
      }
      state S${stateCount} {
        entry / {
          montiarc.lang.Simulation.stop();
        }
      }
      </#if>
      state Failed {
        entry / {
          montiarc.maunit.api.Assertions.fail();
        }
      }

  <#-- manual star transiton -->
  <#if (stateCount > 0)>
  <#list 0..<stateCount as i>
    <#list helper.getVisibleComponents(ast) as comp>
      <#list helper.getComponentPorts(comp) as portSymbol>
        <#if portSymbol.isOutgoing()>
          <#if (i > 0 || restricted) && !helper.isPortSourceAtMessageIndex(i, ast, comp, portSymbol) && !helper.isFree(ast, comp, portSymbol)>
      S${i} -> Failed port_${comp.getName()}_${portSymbol.getName()};
          <#elseif !helper.isSyncPort(portSymbol)>
      S${i} -> S${i} port_${comp.getName()}_${portSymbol.getName()} / {${comp.getName()}_${portSymbol.getName()} = port_${comp.getName()}_${portSymbol.getName()};}
          </#if>
        </#if>
      </#list>
    </#list>
      <#if (i > 0 || restricted) && strictTickGuards[i?c]??>
      <#-- no waiting on fully constrained tick blocks -->
      S${i} -> Failed [!(${strictTickGuards[i?c]})];
      <#else>
      <#-- fail in the last tick, as the simulation ends afterwards -->
      S${i} -> Failed [countedTicks + 1 >= ${helper.tickLength(ast)}];
      S${i} -> S${i} [countedTicks + 1 < ${helper.tickLength(ast)}] / {
        countedTicks = countedTicks + 1;
    <#list helper.getVisibleComponents(ast) as comp>
      <#list helper.getComponentPorts(comp) as portSymbol>
        <#if portSymbol.isOutgoing() && helper.isSyncPort(portSymbol)>
        ${comp.getName()}_${portSymbol.getName()} = port_${comp.getName()}_${portSymbol.getName()};
        </#if>
      </#list>
    </#list>
      <@sendSyncInputs/>
      }
      </#if>

  </#list>
  </#if>
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
