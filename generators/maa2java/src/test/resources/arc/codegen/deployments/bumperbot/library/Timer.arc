package bumperbot.library;
//abstract 
component Timer(long delay) {
    port
        in Integer start,
        out Boolean tick;
}
