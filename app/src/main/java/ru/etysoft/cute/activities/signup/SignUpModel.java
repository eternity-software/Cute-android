package ru.etysoft.cute.activities.signup;

import ru.etysoft.cuteframework.Methods;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.Registration.RegistrationResponse;

public class SignUpModel implements SignUpContract.Model {
    @Override
    public RegistrationResponse signUp(String login, String email, String password) throws ResponseException {
        return Methods.register(login, email, password);
    }
}
