package net.earthrealms.manacore.module.account.exception;

public class InvalidPlayerException extends Exception { 
    public InvalidPlayerException(String errorMessage) {
        super(errorMessage);
    }
}
