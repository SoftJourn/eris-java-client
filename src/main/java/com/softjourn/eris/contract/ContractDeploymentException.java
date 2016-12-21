package com.softjourn.eris.contract;

public class ContractDeploymentException extends RuntimeException {

    public ContractDeploymentException(String message) {
        super(message);
    }

    public ContractDeploymentException(Throwable cause) {
        super("Cant deploy contract due to exception: " + cause.getMessage(), cause);
    }
}
