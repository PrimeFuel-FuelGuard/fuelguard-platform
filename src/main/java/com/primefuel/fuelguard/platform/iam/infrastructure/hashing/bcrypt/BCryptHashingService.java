package com.primefuel.fuelguard.platform.iam.infrastructure.hashing.bcrypt;

import com.primefuel.fuelguard.platform.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface BCryptHashingService extends HashingService, PasswordEncoder {
}
