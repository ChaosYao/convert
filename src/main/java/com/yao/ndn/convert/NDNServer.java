package com.yao.ndn.convert;

import lombok.extern.slf4j.Slf4j;
import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnInterestCallback;
import net.named_data.jndn.OnRegisterFailed;
import net.named_data.jndn.InterestFilter;
import net.named_data.jndn.security.KeyChain;

import net.named_data.jndn.security.pib.PibIdentity;
import net.named_data.jndn.util.Blob;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service; 

@Slf4j
@Service
public class NDNServer {

    @Value("${app.ndn.pib.path}")
    private String pibPath;

    @Value("${app.ndn.tpm.path}")
    private String tpmPath;

    @Value("${app.ndn.default.identity}")
    private String defaultIdentity;
    
    public void init(){
        Face face = new Face("localhost", 6363);
        
        try {
            KeyChain keyChain = new KeyChain(pibPath, tpmPath); 
            Name identityName = new Name(defaultIdentity);
            PibIdentity identity = keyChain.getPib().getIdentity(identityName);
            keyChain.setDefaultIdentity(identity);
            face.setCommandSigningInfo(keyChain, keyChain.getDefaultCertificateName());  

            face.registerPrefix(identityName, new OnInterestCallback() {
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
            }, new OnRegisterFailed() {
                @Override
                public void onRegisterFailed(Name p) {
                    log.error("Failed to register prefix: {}", p.toUri());
                }

            });

            for(int i = 0; i< 50; i++) {
                face.processEvents();
                Thread.sleep(5);
            }

        } catch (Exception e) {
            log.error("Server init failed", e);
        }
    }
}
