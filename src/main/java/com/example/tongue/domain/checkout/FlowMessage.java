package com.example.tongue.domain.checkout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlowMessage {

    private String errorMessage;
    private String errorStage;
    private Boolean solved;
    private Map<String,Object> attributes = new HashMap<>();;

    public String getErrorStage() {
        return errorStage;
    }

    public void setErrorStage(String errorStage) {
        this.errorStage = errorStage;
    }

    public void setAttribute(Object attribute, String key){
        attributes.put(key,attribute);
    }

    public Object getAttribute(String key){
        return attributes.get(key);
    }

    public Boolean isSolved(){
        return solved;
    }

    public void setSolved(Boolean solved){
        this.solved = solved;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
