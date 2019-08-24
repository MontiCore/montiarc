/* (c) https://github.com/MontiCore/monticore */
package types.typecompatibility;

component GenericTypes<T> {

    port in List<String> portListString;
    port in List<Integer> portListInteger;
    port in ArrayList<String> portArrayListString;
    port in List<Object> portListObject;
    port in Map<String, Integer> portMapStringInteger;
    port in HashMap<String, Integer> portHashMapStringInteger;
    port in List<T> portListT;
    port in T portT;


    component Inner<K> {
        port in List<K> portListK;
        port in K portK;
    }
    component Inner<T> innerTInstance;
    component Inner<String> innerStringInstance;
}
