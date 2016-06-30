<#-- ${tc.params(...)} -->

    /*
     * (non-Javadoc)
     * @see ${glex.getGlobalValue("IComponent")}#checkConstraints()
     */
    @Override
    public void checkConstraints() {
        <#-- ${op.includeTemplates(checkConstraintsHook, ast)} -->
  <#list constraints as inv>
        if (!_check${inv.getName()?cap_first}()) {
            sim.error.ArcSimProblemReport report = 
                new sim.error.ArcSimProblemReport(
                    sim.error.ArcSimProblemReport.Type.ERROR, 
                    "Injured constraint ${inv.getName()}",
                    <#if glex.getGlobalValue("TIME_PARADIGM_STORAGE_KEY").isTimed()>
                        getLocalTime(), 
                    </#if>
                    getComponentName()
                    );
            if (getErrorHandler() != null) {
                getErrorHandler().addReport(report);
            }
            else {
                System.err.println(report.toString());
            }
            
        }
  </#list>
    }
  <#list constraints as inv>
        ${tc.writeArgs("mc.umlp.arc.implementation.methods.CheckConstraint", [inv.getName()])}
  </#list>    
