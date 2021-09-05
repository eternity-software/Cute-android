package ru.etysoft.cute.activities.signup;

import ru.etysoft.cuteframework.CuteFramework;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.account.Registration.RegistrationResponse;


public class SignUpModel implements SignUpContract.Model {
    @Override
    public RegistrationResponse signUp(String login, String displayName, String email, String password) throws ResponseException {
        return CuteFramework.register(login, email, password, displayName);
    }
}
