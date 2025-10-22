package org.baeldung.web.dto;

public class MessageDto {
    private String name;
    private String email;
    private String content;
    private Long payeId;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Long getPayeId() { return payeId; }
    public void setPayeId(Long payeId) { this.payeId = payeId; }
}
