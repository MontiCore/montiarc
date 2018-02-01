package components.body.subcomponents;

component ConfigurableComponentWithInnerCfgComp(int myParam) {

    component Inner(int myParam) {
    }
    
    component Inner(myParam) myInner;
}