package com.primefuel.fuelguard.platform.iam.application.commandservices;

import com.primefuel.fuelguard.platform.iam.domain.model.aggregates.User;
import com.primefuel.fuelguard.platform.iam.domain.model.commands.SignInCommand;
import com.primefuel.fuelguard.platform.iam.domain.model.commands.SignUpCommand;
import com.primefuel.fuelguard.platform.shared.application.result.ApplicationError;
import com.primefuel.fuelguard.platform.shared.application.result.Result;
public interface UserCommandService {
    Result<SignInResult, ApplicationError> handle(SignInCommand command);
    Result<User, ApplicationError> handle(SignUpCommand command);

    record SignInResult(User user, String token) { }
}
