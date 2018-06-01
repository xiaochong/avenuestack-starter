package avenuestack;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {


    @Autowired
    AvenueStack avenueStack;

    @Autowired
    ApplicationContext applicationContext;

    @Before
    public void setUp() throws Exception {
        Thread.sleep(2000L);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        HashMap<String, Object> body = new HashMap<>(1);
        body.put("message", "xiaochong");
        Request req = RequestHelper.newRequest("test.hello", body);
        Response response = avenueStack.sendRequestWithReturn(req, 1000);
        assertEquals("hello xiaochong", response.body().ns("message"));
    }
}