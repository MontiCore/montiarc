<#-- (c) https://github.com/MontiCore/monticore -->
${signature("fmu")}
<#include "/templates/Utils.ftl">
${p}

public interface ${fmu.getName()}Comp
        extends montiarc.rte.component.Component,
        ${fmu.getName()}Output,
        ${fmu.getName()}Input {
}
