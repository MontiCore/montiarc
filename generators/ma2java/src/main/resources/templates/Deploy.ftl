<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates the deployment class for a component. -->
${tc.signature("comp", "helper", "isTOPClass")}
<#assign comp=comp><#assign helper=helper>
<#import "/templates/util/Utils.ftl" as Utils>

<#if comp.getPackageName() != "">
  package ${comp.getPackageName()};
</#if>

<#assign compName = comp.getName()>
<#assign deployClassName> <#if isTOPClass> Deploy${compName}TOP <#else> Deploy${compName} </#if> </#assign>

public class ${deployClassName} {

  final static int CYCLE_TIME = 50; // in ms

  public static void main(String[] args) {
    final ${compName} cmp = new ${compName}();

    cmp.setUp();
    cmp.init();

    long time;

    while (!Thread.interrupted()) {
      time = System.currentTimeMillis();
      cmp.compute();
      cmp.update();
      while((System.currentTimeMillis()-time) < CYCLE_TIME){
        Thread.yield();
      }
    }
  }
}