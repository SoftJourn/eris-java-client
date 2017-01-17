package com.softjourn.eris.transaction.type;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigInteger;

/**
 * Height returns from get latest block
 * Created by vromanchuk on 17.01.17.
 */
@Data
@AllArgsConstructor
public class Height {
    BigInteger height;
}
