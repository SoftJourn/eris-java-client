package com.softjourn.eris.contract.response;

import lombok.Data;

@Data
public class ReturnValue<T> {

    private final T val;

    public ReturnValue(Class<T> tClass, T val) {
        this.val = val;
    }

}
