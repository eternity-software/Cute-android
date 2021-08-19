package ru.etysoft.cute.activities.confirmation;

import ru.etysoft.cuteframework.Methods;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.account.Confirmation.ConfirmationResponse;


public class ConfirmationModel implements ConfirmationContract.Model {
    @Override
    public ConfirmationResponse confirm(int code, String token) throws ResponseException {
        return Methods.confirmation(token, code);
    }
}
