package ru.etysoft.cute.activities.main;


import ru.etysoft.cuteframework.CuteFramework;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.account.GetAccount.GetAccountResponse;


public class MainModel implements MainContract.Model {
    @Override
    public GetAccountResponse getAccountInfo(String token) throws ResponseException {
        return CuteFramework.getInfo(token);
    }
}
