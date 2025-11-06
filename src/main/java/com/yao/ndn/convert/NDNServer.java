package com.yao.ndn.convert;

import lombok.extern.slf4j.Slf4j;
import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnInterestCallback;
import net.named_data.jndn.InterestFilter;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.identity.IdentityManager;
import net.named_data.jndn.security.identity.MemoryIdentityStorage;
import net.named_data.jndn.security.identity.MemoryPrivateKeyStorage;
import net.named_data.jndn.security.policy.SelfVerifyPolicyManager;
import net.named_data.jndn.util.Blob;
import org.springframework.stereotype.Service; 

@Slf4j
@Service
public class NDNServer {
    public static void main(String[] args) {
        Face face = new Face("localhost", 6363);
        MemoryIdentityStorage memoryIdentityStorage = new MemoryIdentityStorage();
        MemoryPrivateKeyStorage privateKeyStorage = new MemoryPrivateKeyStorage();

        KeyChain keyChain = new KeyChain(new IdentityManager(memoryIdentityStorage, privateKeyStorage), 
        new SelfVerifyPolicyManager(memoryIdentityStorage));
        
        try {
            face.setCommandSigningInfo(keyChain, keyChain.getDefaultCertificateName());  
            Name prefix = new Name("/yao/test/demo/B");
            face.registerPrefix(prefix, new OnInterestCallback() {
                @Override
                public void onInterest(Name n, Interest i, Face f, long interestFilterId, InterestFilter filter) {
                    try {
                        log.info("Receive Interest: {}", i.getName().toUri());
                        Name dataName = new Name(i.getName()).append("reply");
                        Data data = new Data(dataName);
                        data.setContent(new Blob("Congradulations!"));
                        keyChain.sign(data);

                        face.putData(data);
                        log.info("Send data");

                    } catch (Exception e) {
                        log.error("Error handling interest", e);
                    }
                }
            }, null);

            for(int i = 0; i< 50; i++) {
                face.processEvents();
                Thread.sleep(5);
            }

        } catch (Exception e) {
            log.error("Server init failed", e);
        }
    }
}
