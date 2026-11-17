package org.example.greeting;

public class GreetingService {

    private final GreetingProperties props;

    public GreetingService(GreetingProperties props) {
        this.props = props;
    }

    public String greet(String name) {
        return props.getMessage() + "，" + name + "！";
    }
}
