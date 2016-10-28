package com.softjourn.eris.accounts;

import com.softjourn.eris.ErisAccountData;
import com.softjourn.eris.contract.Util;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.KeyPairGenerator;
import org.bouncycastle.util.encoders.Hex;

import java.security.KeyPair;
import java.security.SecureRandom;

public class KeyService {

    private KeyPairGenerator keyPairGenerator;

    public KeyService() {
        keyPairGenerator = new KeyPairGenerator();

        keyPairGenerator.initialize(256, new SecureRandom());
    }

    ErisAccountData generateNewKey() {

        Pair keyPair = genKeyPair();

        String address = getAddress(keyPair.pub);

        return new ErisAccountData() {
            @Override
            public String getAddress() {
                return address.toUpperCase();
            }

            @Override
            public String getPubKey() {
                return Hex.toHexString(keyPair.pub).toUpperCase();
            }

            @Override
            public String getPrivKey() {
                return Hex.toHexString(keyPair.priv).toUpperCase();
            }
        };
    }

    private Pair genKeyPair() {

        KeyPair keypair = keyPairGenerator.generateKeyPair();

        EdDSAPrivateKey privateKey = (EdDSAPrivateKey) keypair.getPrivate();
        EdDSAPublicKey publicKey = (EdDSAPublicKey) keypair.getPublic();

        byte[] privateKeyValue = Hex.decode(Hex.toHexString(privateKey.getSeed()) + Hex.toHexString(privateKey.getA().toByteArray()));

        return new Pair(privateKeyValue, publicKey.getAbyte());
    }


    private static String getAddress(byte[] publicKey) {
        return Util.tendermintRIPEDM160Hash(publicKey);
    }


    private static class Pair {
        final byte[] priv;
        final byte[] pub;

        Pair(byte[] priv, byte[] pub) {
            this.priv = priv;
            this.pub = pub;
        }
    }

}
