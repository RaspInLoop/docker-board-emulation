package org.raspinloop.emulator.proxyserver.qemu;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;



@Component
public class QemuHealthCheck extends AbstractHealthIndicator {
	
	@Autowired
	QemuProcess qemu;	
	
    @Override
    protected void doHealthCheck(Health.Builder bldr) throws Exception {
        if (qemu.getStartStatus() == null) {
        	bldr.up()
        		.withDetail("state", "WAITING_FOR_START");
        } else if ( qemu.getStartStatus().isStarted() && qemu.isAlive()) {        
        	bldr.up()
          		.withDetail("state", "RUNNING")
          		.withDetail("started_for", Duration.between(qemu.getStartStatus().getStartTime(), Instant.now()) );
        } else {
          bldr.down()
          	   .withDetail("state", "STOPPED");
        }
    }
}
