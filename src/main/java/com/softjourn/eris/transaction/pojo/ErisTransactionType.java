package com.softjourn.eris.transaction.pojo;

import lombok.Getter;

import java.util.Arrays;

/**
 * This type is beginning byte of body
 * link https://github.com/eris-ltd/eris-db/blob/f9c5c013523b73d380889bf17e66c2772aaf3d8d/txs/tx.go
 */
public enum ErisTransactionType {
    // Account transactions
    SEND (0x01),CALL(0x02),NAME(0x03),
    // Validation transactions
    BOND(0x11),UNBOND(0x12),REBOND(0x13),DUPEOUT(0x14),
    // Admin transactions
    PERMISSIONS(0x20), UNDEFINED(0x00);

    @Getter
    private final byte code;

    ErisTransactionType(int code){
        this.code = (byte) code;
    }

    public static ErisTransactionType findByCode(int code){
        return Arrays.stream(ErisTransactionType.values())
                .filter(type -> type.code == code)
                .findFirst()
                .orElse(null);
    }
}
