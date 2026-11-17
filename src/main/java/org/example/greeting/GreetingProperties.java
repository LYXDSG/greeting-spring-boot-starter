package org.example.greeting;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "greeting")
public class GreetingProperties {

    private boolean enabled = true;
    private String message = "你好";

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
