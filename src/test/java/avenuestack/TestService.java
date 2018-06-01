package avenuestack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.HashMap;

@AvenueService("test")
public class TestService {

    private final AvenueStack avenueStack;

    @Autowired
    public TestService(AvenueStack avenueStack) {
        Assert.notNull(avenueStack, "avenueStack must be not null!");
        this.avenueStack = avenueStack;
    }

    @AvenueMessage("hello")
    public void hello(Request request) {
        HashMap<String, Object> hashMap = new HashMap<>(1);
        hashMap.put("message", "hello " + request.body().s("message"));
        avenueStack.sendResponse(0, hashMap, request);
    }
}
