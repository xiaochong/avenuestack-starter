package avenuestack;

import java.lang.reflect.Method;

/**
 * @author chenjinquan.jim
 */
public class MessageBean {
    private Object bean;
    private Method method;

    public MessageBean(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "bean=" + bean +
                ", method=" + method +
                '}';
    }
}
