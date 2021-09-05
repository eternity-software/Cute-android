package ru.etysoft.cute.activities.confirmation;


import ru.etysoft.cuteframework.CuteFramework;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.account.Confirmation.ConfirmationResponse;


public class ConfirmationModel implements ConfirmationContract.Model {
    @Override
    public ConfirmationResponse confirm(int code, String token) throws ResponseException {
        return CuteFramework.confirmation(token, code);
    }
}
