package chat.client;

public class IDGenerator {
    private static IDGenerator instance = new IDGenerator();

    public static IDGenerator getInstance() {
        return instance;
    }

    private Integer counter = 0;
    
    public Integer getId() {
        return ++this.counter;
    }
    
    public Integer getCurrent() {
        return this.counter;
    }
    

}
