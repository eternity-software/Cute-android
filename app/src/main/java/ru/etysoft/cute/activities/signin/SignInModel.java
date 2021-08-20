package ru.etysoft.cute.activities.signin;

import ru.etysoft.cuteframework.Methods;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.account.Login.LoginResponse;


public class SignInModel implements SignInContract.Model {
    @Override
    public LoginResponse signIn(String login, String password) throws ResponseException {
        return Methods.authorize(login, password);
    }
}
