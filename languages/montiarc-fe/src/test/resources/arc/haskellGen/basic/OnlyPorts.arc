package basic;

component OnlyPorts {
    port
    in T inp1,
    in T inp2,
    out T outp1,
    out T outp2;

    connect inp1 -> outp1;
    connect inp2 -> outp2;
}