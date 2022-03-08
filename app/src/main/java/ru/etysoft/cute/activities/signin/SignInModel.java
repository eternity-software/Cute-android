package ru.etysoft.cute.activities.signin;


import java.sql.SQLException;

import ru.etysoft.cuteframework.CuteFramework;
import ru.etysoft.cuteframework.exceptions.NotCachedException;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.account.LoginRequest;



public class SignInModel implements SignInContract.Model {
    @Override
    public LoginRequest.LoginResponse signIn(String login, String password) throws ResponseException, NotCachedException, SQLException {
        return new LoginRequest(login, password).execute();
    }
}
