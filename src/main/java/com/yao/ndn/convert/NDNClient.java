package com.yao.ndn.convert;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import net.named_data.jndn.*;

@Slf4j
@Service
public class NDNClient {
    public static void main(String[] args) {
        try {
            Interest reqInterest = new Interest(new Name("/yao/test/demo/B"));
            reqInterest.setCanBePrefix(true);
            reqInterest.setInterestLifetimeMilliseconds(4000);
            log.info("Send Interest: " + reqInterest.toString());

            Face face = new Face("localhost", 6363);
            face.expressInterest(reqInterest, new OnData() {
                @Override
                public void onData(Interest a, Data data) {
                    log.info("Receive the data: {}", data.toString());
                }
            }, new OnTimeout() {
                @Override
                public void onTimeout(Interest a) {
                    log.error("Time out interest: {}", a.toString());
                }
            });

            for (int i = 0; i < 50; i++) {
                face.processEvents();
                Thread.sleep(5);
            }

        } catch (Exception e) {
            log.error("Failed to send NDN interest", e);
        }
    }
}
