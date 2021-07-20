package ru.etysoft.cute.activities.signin;

import ru.etysoft.cuteframework.Methods;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.Authorization.AuthorizationResponse;

public class SignInModel implements SignInContract.Model {
    @Override
    public AuthorizationResponse signIn(String login, String password) throws ResponseException {
        return Methods.authorize(login, password);
    }
}
