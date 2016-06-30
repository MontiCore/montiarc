package a;

component CompWithGenericPorts<K extends Number, V extends Iterable, W> {

    port
        in K myKInput,
        in W myWInput,
        out V myVOutput;  

}