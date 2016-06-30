${tc.signature("package", "symbol")}


package ${package};

public class Deploy${symbol.getName()} {

     final static int CYCLE_TIME = 100;

    public static void main(String[] args) {
        ${symbol.getName()} cmp = new ${symbol.getName()}();
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