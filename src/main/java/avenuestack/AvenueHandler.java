package avenuestack;

import avenuestack.impl.netty.AvenueStackImpl;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenjinquan.jim
 */
public class AvenueHandler implements RequestReceiver {

    private final Logger logger = LoggerFactory.getLogger(AvenueHandler.class);

    private final ApplicationContext applicationContext;
    private final AvenueStackImpl avenueStack;

    private final ConcurrentHashMap<String, MessageBean> messageBeans = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> codeMapping = new ConcurrentHashMap<>();

    public AvenueHandler(ApplicationContext applicationContext, AvenueStackImpl avenueStack) {
        Assert.notNull(applicationContext, "applicationContext must be not null!");
        Assert.notNull(avenueStack, "avenueStack must be not null!");
        this.applicationContext = applicationContext;
        this.avenueStack = avenueStack;
    }

    @Override
    public void receiveRequest(Request request) {
        int serviceId = request.getServiceId();
        int msgId = request.getMsgId();
        String msgKey = codeMapping.get(getKey(String.valueOf(serviceId), String.valueOf(msgId)));
        if (msgKey == null) {
            logger.error("avenue message[{}.{}] not definition in avenue_conf", serviceId, msgId);
            avenueStack.sendResponse(ErrorCodes.SERVICE_NOT_FOUND, null, request);
            return;
        }
        MessageBean messageBean = messageBeans.get(msgKey);
        if (messageBean == null) {
            logger.error("avenue message[{}] not implements", msgKey);
            avenueStack.sendResponse(ErrorCodes.SERVICE_NOT_FOUND, null, request);
            return;
        }
        try {
            messageBean.getMethod().invoke(messageBean.getBean(), request);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() throws Exception {
        // init RequestReceiver
        avenueStack.setRequestReceiver(this);

        // init message Bean
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(AvenueService.class);
        beans.forEach((s, o) -> {
            Class<?> c = o.getClass();
            AvenueService srv = c.getAnnotation(AvenueService.class);
            Method[] methods = c.getMethods();
            for (Method method : methods) {
                AvenueMessage msg = method.getAnnotation(AvenueMessage.class);
                if (msg != null) {
                    messageBeans.put(getKey(srv.value(), msg.value()), new MessageBean(o, method));
                }
            }
        });

        //init avenueXml
        List<String> avenueXmlFiles = avenueStack.getAvenueXmlFiles();
        SAXReader saxReader = new SAXReader();
        saxReader.setEncoding("UTF-8");
        for (String xmlFile : avenueXmlFiles) {
            Element srv = saxReader.read(new FileReader(xmlFile)).getRootElement();
            Attribute srvId = srv.attribute("id");
            Attribute srvName = srv.attribute("name");
            List<Element> messages = srv.selectNodes("message");
            for (Element msg : messages) {
                Attribute msgId = msg.attribute("id");
                Attribute msgName = msg.attribute("name");
                codeMapping.put(getKey(srvId.getValue(), msgId.getValue()), getKey(srvName.getValue(), msgName.getValue()));
            }
        }
    }

    private String getKey(String srv, String msg) {
        return srv + "." + msg;
    }
}
