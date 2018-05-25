package avenuestack;

/**
 * @author chenjinquan.jim
 */
public class AvenueHandler implements RequestReceiver {

    @Override
    public void receiveRequest(Request request) {
        System.out.println("------------->" + request);

    }
}
