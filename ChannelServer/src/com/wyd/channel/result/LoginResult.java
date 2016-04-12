package com.wyd.channel.result;
public class LoginResult{
    private String code;
    private String message;
    private String thirdReturnMessage;
    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getThirdReturnMessage() {
        return thirdReturnMessage;
    }

    public void setThirdReturnMessage(String thirdReturnMessage) {
        this.thirdReturnMessage = thirdReturnMessage;
    }
    
}
