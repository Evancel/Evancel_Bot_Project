package pro.sky.enums;

public enum ServiceCommands {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start"),
    TO_SET_A_TASK_FOR_SCHEDULER("/task");

    private final String value;

    ServiceCommands(String value){
        this.value=value;
    }

    @Override
    public String toString(){
        return value;
    }

    public static ServiceCommands fromValue(String v){
        for(ServiceCommands c:ServiceCommands.values()){
            if(c.value.equals(v)){
                return  c;
            }
        }
        return null;
    }
}
