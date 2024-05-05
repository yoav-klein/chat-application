package chat.common.util;

public class IDGenerator {

    private Integer counter = 0;
    
    public Integer getId() {
        return ++this.counter;
    }
    
    public Integer getCurrent() {
        return this.counter;
    }
    

}
