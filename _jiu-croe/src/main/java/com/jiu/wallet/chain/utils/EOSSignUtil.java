package com.jiu.wallet.chain.utils;

import com.jiu.wallet.btc.utils.ByteUtil;
import com.jiu.wallet.btc.utils.NumericUtil;
import lombok.Data;
import lombok.SneakyThrows;
import org.bitcoinj.core.*;
import org.spongycastle.crypto.digests.RIPEMD160Digest;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.bitcoinj.core.ECKey;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.params.*;
import org.spongycastle.crypto.signers.DSAKCalculator;
import org.spongycastle.math.ec.ECAlgorithms;
import org.spongycastle.math.ec.ECMultiplier;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.ec.FixedPointCombMultiplier;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.signers.DSAKCalculator;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.BigIntegers;

import java.math.BigInteger;
import java.security.SecureRandom;

import java.math.BigInteger;
import java.security.SecureRandom;

import static java.math.BigDecimal.ZERO;
import static java.math.BigInteger.ONE;
import static org.bitcoinj.core.ECKey.CURVE;


public class EOSSignUtil {


    public static String sign(byte[] dataSha256, byte[] prvKey) {
        ECKey ecKey = EOSKey.fromPrivate(prvKey).getECKey();
        SignatureData signatureData = signAsRecoverable(dataSha256, ecKey);
        byte[] sigResult = ByteUtil.concat(NumericUtil.intToBytes(signatureData.getV()), signatureData.getR());
        sigResult = ByteUtil.concat(sigResult, signatureData.getS());
        return serialEOSSignature(sigResult);
    }

    @SneakyThrows
    private static SignatureData signAsRecoverable(byte[] value, ECKey ecKey) {
        int recId = -1;
        ECKey.ECDSASignature sig = eosSign(value, ecKey.getPrivKey());
        for (int i = 0; i < 4; i++) {
            ECKey recoverKey = ECKey.recoverFromSignature(i, sig, Sha256Hash.wrap(value), false);
            if (recoverKey != null && recoverKey.getPubKeyPoint().equals(ecKey.getPubKeyPoint())) {
                recId = i;
                break;
            }
        }

        if (recId == -1) {
            throw new Exception("Could not construct a recoverable key. This should never happen.");
        }
        int headerByte = recId + 27 + 4;
        // 1 header + 32 bytes for R + 32 bytes for S
        byte v = (byte) headerByte;
        byte[] r = NumericUtil.bigIntegerToBytesWithZeroPadded(sig.r, 32);
        byte[] s = NumericUtil.bigIntegerToBytesWithZeroPadded(sig.s, 32);

        return new SignatureData(v, r, s);

    }

    private static ECKey.ECDSASignature eosSign(byte[] input, BigInteger privateKeyForSigning) {
        EOSECDSASigner signer = new EOSECDSASigner(new MyHMacDSAKCalculator(new SHA256Digest()));
        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(privateKeyForSigning, CURVE);
        signer.init(true, privKey);
        BigInteger[] components = signer.generateSignature(input);
        return new ECKey.ECDSASignature(components[0], components[1]).toCanonicalised();
    }

    private static String serialEOSSignature(byte[] data) {
        byte[] toHash = ByteUtil.concat(data, "K1".getBytes());
        RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update(toHash, 0, toHash.length);
        byte[] out = new byte[20];
        digest.doFinal(out, 0);
        byte[] checksumBytes = Arrays.copyOfRange(out, 0, 4);
        data = ByteUtil.concat(data, checksumBytes);
        return "SIG_K1_" + Base58.encode(data);
    }

    public static class EOSKey extends VersionedChecksummedBytes {

        protected EOSKey(String encoded) throws AddressFormatException {
            super(encoded);
        }

        protected EOSKey(int version, byte[] bytes) {
            super(version, bytes);
        }

        public static EOSKey fromWIF(String wif) {
            return new EOSKey(wif);
        }

        public static EOSKey fromPrivate(byte[] prvKey) {
            // EOS doesn't distinguish between mainnet and testnet.
            return new EOSKey(128, prvKey);
        }

        public static String privateToPublicKey(byte[] prvKey) {
            return new EOSKey(128, prvKey).getPublicKeyAsHex();
        }

        public String getPublicKeyAsHex() {
            ECKey ecKey = ECKey.fromPrivate(bytes);
            byte[] pubKeyData = ecKey.getPubKey();
            RIPEMD160Digest digest = new RIPEMD160Digest();
            digest.update(pubKeyData, 0, pubKeyData.length);
            byte[] out = new byte[20];
            digest.doFinal(out, 0);
            byte[] checksumBytes = Arrays.copyOfRange(out, 0, 4);

            pubKeyData = ByteUtil.concat(pubKeyData, checksumBytes);
            return "EOS" + Base58.encode(pubKeyData);
        }

        public byte[] getPrivateKey() {
            return bytes;
        }

        ECKey getECKey() {
            return ECKey.fromPrivate(bytes, true);
        }

    }

    @Data
    public static class SignatureData {
        private final int v;
        private final byte[] r;
        private final byte[] s;
        @Override
        public String toString() {
            String r = NumericUtil.bytesToHex(getR());
            String s = NumericUtil.bytesToHex(getS());
            return String.format("%s%s%02x", r, s, getV());
        }
    }

    public static class EOSECDSASigner {
        private final DSAKCalculator kCalculator;

        private ECKeyParameters key;
        private SecureRandom random;

        /**
         * Configuration with an alternate, possibly deterministic calculator of K.
         *
         * @param kCalculator a K value calculator.
         */
        public EOSECDSASigner(DSAKCalculator kCalculator) {
            this.kCalculator = kCalculator;
        }

        public void init(
                boolean forSigning,
                CipherParameters param) {
            SecureRandom providedRandom = null;

            if (forSigning) {
                if (param instanceof ParametersWithRandom) {
                    ParametersWithRandom rParam = (ParametersWithRandom) param;

                    this.key = (ECPrivateKeyParameters) rParam.getParameters();
                    providedRandom = rParam.getRandom();
                } else {
                    this.key = (ECPrivateKeyParameters) param;
                }
            } else {
                this.key = (ECPublicKeyParameters) param;
            }

            this.random = initSecureRandom(forSigning && !kCalculator.isDeterministic(), providedRandom);
        }

        // 5.3 pg 28

        /**
         * generate a signature for the given message using the key we were
         * initialised with. For conventional DSA the message should be a SHA-1
         * hash of the message of interest.
         *
         * @param message the message that will be verified later.
         */
        public BigInteger[] generateSignature(
                byte[] message) {
            ECDomainParameters ec = key.getParameters();
            BigInteger n = ec.getN();
            BigInteger e = calculateE(n, message);
            BigInteger d = ((ECPrivateKeyParameters) key).getD();

            int nonce = 1;
            BigInteger r, s;
            while (true) {

                kCalculator.init(n, d, message);
                ECMultiplier basePointMultiplier = createBasePointMultiplier();

                // 5.3.2
                do // generate s
                {
                    BigInteger k = BigInteger.ZERO;
                    do // generate r
                    {
                        k = kCalculator.nextK();
                        for (int i = 0; i < nonce; i++) {
                            k = kCalculator.nextK();
                        }

                        ECPoint p = basePointMultiplier.multiply(ec.getG(), k).normalize();

                        // 5.3.3
                        r = p.getAffineXCoord().toBigInteger().mod(n);
                    }
                    while (r.equals(ZERO));

                    // Compute s = (k^-1)*(h + Kx*privkey)
                    s = k.modInverse(n).multiply(e.add(d.multiply(r))).mod(n);
                }
                while (s.equals(ZERO));

                byte[] der = new ECKey.ECDSASignature(r, s).toCanonicalised().encodeToDER();

                int lenR = der[3];
                int lenS = der[5 + lenR];
                if (lenR == 32 && lenS == 32) {
                    break;
                }
                nonce++;
            }

            return new BigInteger[]{r, s};
        }

        // 5.4 pg 29

        /**
         * return true if the value r and s represent a DSA signature for
         * the passed in message (for standard DSA the message should be
         * a SHA-1 hash of the real message to be verified).
         */
        public boolean verifySignature(
                byte[] message,
                BigInteger r,
                BigInteger s) {
            ECDomainParameters ec = key.getParameters();
            BigInteger n = ec.getN();
            BigInteger e = calculateE(n, message);

            // r in the range [1,n-1]
            if (r.compareTo(ONE) < 0 || r.compareTo(n) >= 0) {
                return false;
            }

            // s in the range [1,n-1]
            if (s.compareTo(ONE) < 0 || s.compareTo(n) >= 0) {
                return false;
            }

            BigInteger c = s.modInverse(n);

            BigInteger u1 = e.multiply(c).mod(n);
            BigInteger u2 = r.multiply(c).mod(n);

            ECPoint G = ec.getG();
            ECPoint Q = ((ECPublicKeyParameters) key).getQ();

            ECPoint point = ECAlgorithms.sumOfTwoMultiplies(G, u1, Q, u2).normalize();

            // components must be bogus.
            if (point.isInfinity()) {
                return false;
            }

            BigInteger v = point.getAffineXCoord().toBigInteger().mod(n);

            return v.equals(r);
        }

        protected BigInteger calculateE(BigInteger n, byte[] message) {
            int log2n = n.bitLength();
            int messageBitLength = message.length * 8;

            BigInteger e = new BigInteger(1, message);
            if (log2n < messageBitLength) {
                e = e.shiftRight(messageBitLength - log2n);
            }
            return e;
        }

        protected ECMultiplier createBasePointMultiplier() {
            return new FixedPointCombMultiplier();
        }

        protected SecureRandom initSecureRandom(boolean needed, SecureRandom provided) {
            return !needed ? null : (provided != null) ? provided : new SecureRandom();
        }
    }

    public static  class MyHMacDSAKCalculator implements DSAKCalculator {
        private static final BigInteger ZERO = BigInteger.valueOf(0);

        private final HMac hMac;
        private final byte[] K;
        private final byte[] V;

        private BigInteger n;

        private boolean needTry;

        /**
         * Base constructor.
         *
         * @param digest digest to build the HMAC on.
         */
        public MyHMacDSAKCalculator(Digest digest) {
            this.hMac = new HMac(digest);
            this.V = new byte[hMac.getMacSize()];
            this.K = new byte[hMac.getMacSize()];
        }

        public boolean isDeterministic() {
            return true;
        }

        public void init(BigInteger n, SecureRandom random) {
            throw new IllegalStateException("Operation not supported");
        }

        public void init(BigInteger n, BigInteger d, byte[] message) {
            this.n = n;
            this.needTry = false;

            Arrays.fill(V, (byte) 0x01);
            Arrays.fill(K, (byte) 0);

            byte[] x = new byte[(n.bitLength() + 7) / 8];
            byte[] dVal = BigIntegers.asUnsignedByteArray(d);

            System.arraycopy(dVal, 0, x, x.length - dVal.length, dVal.length);

            byte[] m = new byte[(n.bitLength() + 7) / 8];

            BigInteger mInt = bitsToInt(message);

            if (mInt.compareTo(n) > 0) {
                mInt = mInt.subtract(n);
            }

            byte[] mVal = BigIntegers.asUnsignedByteArray(mInt);

            System.arraycopy(mVal, 0, m, m.length - mVal.length, mVal.length);

            hMac.init(new KeyParameter(K));

            hMac.update(V, 0, V.length);
            hMac.update((byte) 0x00);
            hMac.update(x, 0, x.length);
            hMac.update(m, 0, m.length);

            hMac.doFinal(K, 0);

            hMac.init(new KeyParameter(K));

            hMac.update(V, 0, V.length);

            hMac.doFinal(V, 0);

            hMac.update(V, 0, V.length);
            hMac.update((byte) 0x01);
            hMac.update(x, 0, x.length);
            hMac.update(m, 0, m.length);

            hMac.doFinal(K, 0);

            hMac.init(new KeyParameter(K));

            hMac.update(V, 0, V.length);

            hMac.doFinal(V, 0);
        }

        public BigInteger nextK() {
            byte[] t = new byte[((n.bitLength() + 7) / 8)];

            if (needTry) {
                hMac.init(new KeyParameter(K));
                hMac.update(V, 0, V.length);
                hMac.update((byte) 0x00);

                hMac.doFinal(K, 0);

                hMac.init(new KeyParameter(K));

                hMac.update(V, 0, V.length);

                hMac.doFinal(V, 0);
            }

            int tOff = 0;

            while (tOff < t.length) {
                hMac.init(new KeyParameter(K));
                hMac.update(V, 0, V.length);

                hMac.doFinal(V, 0);

                int len = Math.min(t.length - tOff, V.length);
                System.arraycopy(V, 0, t, tOff, len);
                tOff += len;
            }

            BigInteger k = bitsToInt(t);
            needTry = true;
            return k;

        }

        private BigInteger bitsToInt(byte[] t) {
            BigInteger v = new BigInteger(1, t);

            if (t.length * 8 > n.bitLength()) {
                v = v.shiftRight(t.length * 8 - n.bitLength());
            }

            return v;
        }
    }
}