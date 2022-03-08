package ru.etysoft.cute.activities.signup;

import java.sql.SQLException;

import ru.etysoft.cuteframework.CuteFramework;
import ru.etysoft.cuteframework.exceptions.NotCachedException;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.account.LoginRequest;
import ru.etysoft.cuteframework.methods.account.RegisterRequest;


public class SignUpModel implements SignUpContract.Model {
    @Override
    public LoginRequest.LoginResponse signUp(String login, String email, String password) throws ResponseException, NotCachedException, SQLException {
        return new RegisterRequest(login, email, password).execute();
    }
}
