package com.jiu.wallet.chain.utils;

import com.jiu.wallet.btc.utils.NumericUtil;
import com.jiu.wallet.chain.ChainType;
import com.jiu.wallet.chain.creator.BitcoinAddressCreator;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;

import java.util.List;

/**
 * @Author Administrator
 * @create 2020/9/29 14:31
 */
@Slf4j
@Data
public class AddressUtils {
    private List<String> mnemonicCodes;
    private String segWit;
    private BIP44Util.Metadata metadata;
    private String address;


    public  void btcAddress() {
        String path;
        if (BIP44Util.Metadata.P2WPKH.equals(this.segWit)) {
            path = metadata.isMainNet() ? BIP44Util.BITCOIN_SEGWIT_MAIN_PATH : BIP44Util.BITCOIN_SEGWIT_TESTNET_PATH;
        } else {
            path = metadata.isMainNet() ? BIP44Util.BITCOIN_MAINNET_PATH : BIP44Util.BITCOIN_TESTNET_PATH;
        }
        this.createALLAddress(path);
    }
    @SneakyThrows
    private void createALLAddress(String path){
        MnemonicUtil.validateMnemonics(this.mnemonicCodes);
        DeterministicSeed seed = new DeterministicSeed(mnemonicCodes, null, "", 0L);
        DeterministicKeyChain keyChain = DeterministicKeyChain.builder().seed(seed).build();
        DeterministicKey parent = keyChain.getKeyByPath(BIP44Util.generatePath(path), true);
        NetworkParameters networkParameters = MetaUtil.getNetWork(metadata);
        String xpub = parent.serializePubB58(networkParameters);
        String xprv = parent.serializePrivB58(networkParameters);
        log.error("xpub  [{}]",xpub);
        log.error("xprv  [{}]",xprv);
        DeterministicKey mainAddressKey = keyChain.getKeyByPath(BIP44Util.generatePath(path + "/0/0"), true);
        if (BIP44Util.Metadata.P2WPKH.equals(metadata.getSegWit())) {
            this.address = new BitcoinAddressCreator(networkParameters).fromPrivateKey(mainAddressKey.getPrivateKeyAsHex());
        } else {
            this.address = mainAddressKey.toAddress(networkParameters).toBase58();
        }
        log.error("address  [{}]",address);
        log.error("private  [{}]",NumericUtil.bigIntegerToHex(mainAddressKey.getPrivKey()));
    }


    /**
     * #公钥获取的地址只能用来收钱#
     * 根据主公钥获取钱包交易地址
     * @param xpub  主公钥
     * @param nextIdx  下标
     * @return
     */
    public String newReceiveAddress(String xpub,int nextIdx) {
        NetworkParameters networkParameters = MetaUtil.getNetWork(this.metadata);
        DeterministicKey key = DeterministicKey.deserializeB58(xpub, networkParameters);
        DeterministicKey changeKey = HDKeyDerivation.deriveChildKey(key, ChildNumber.ZERO);
        DeterministicKey indexKey = HDKeyDerivation.deriveChildKey(changeKey, new ChildNumber(nextIdx));
        if (BIP44Util.Metadata.P2WPKH.equals(metadata.getSegWit())) {
            return new BitcoinAddressCreator(networkParameters).fromPrivateKey(indexKey).toBase58();
        } else {
            return indexKey.toAddress(networkParameters).toBase58();
        }
    }

    public static void main(String[] args) {
        AddressUtils addressUtils = new AddressUtils();
        BIP44Util.Metadata metadata = new BIP44Util.Metadata();
        metadata.setChainType(ChainType.BITCOIN);
        metadata.setMainNet(true);
        metadata.setSegWit("P2WPKH");
        addressUtils.setMetadata(metadata);
        System.err.println(addressUtils.newReceiveAddress("xpub6CunHvVYKCsN72GVwBjfr5jdoFzY6TuPLwL7wCvDyo9SmEPQqdhHaZtqdSxDGK71TdysSPNcYiMK3Q7RxKLBt2tcExtCfbLrVfct9kZcwfP",0));;
        System.err.println(addressUtils.newPrivate("xprv9yvRtQxeUqK4tYC2qACfUwnuFEA3h1BXyiQX8pWcRTcTtS4GJ6P32maMnBwr5inCbrwqpbCiSeKf9wEvCQqVMChDLBfCwEdvETfCUNSWmDX",0));
    }



    /**
     * 主私钥获取单个地址的私钥
     * @param xprv
     * @param nextIdx
     * @return
     */
    private String  newPrivate(String xprv,int nextIdx){
        DeterministicKey xprvKey = DeterministicKey.deserializeB58(xprv, MetaUtil.getNetWork(this.metadata));
        DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(xprvKey, new ChildNumber(0, false));
        DeterministicKey externalChangeKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(nextIdx, false));
        return NumericUtil.bigIntegerToHex(externalChangeKey.getPrivKey());
    }

    private String newPrivate(byte[] decrypted){
        if (this.metadata.getSource().equals(BIP44Util.Metadata.FROM_WIF)) {
            return new String(decrypted);
        } else {
            return NumericUtil.bytesToHex(decrypted);
        }
    }
}
