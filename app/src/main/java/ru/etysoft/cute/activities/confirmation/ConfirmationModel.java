package ru.etysoft.cute.activities.confirmation;

import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.Confirmation.ConfirmationResponse;

public class ConfirmationModel implements ConfirmationContract.Model {
    @Override
    public ConfirmationResponse sendCode(String code) throws ResponseException {
        throw new ResponseException("Not implemented yet");
    }
}
