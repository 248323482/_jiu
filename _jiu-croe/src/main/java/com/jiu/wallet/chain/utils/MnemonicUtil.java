package com.jiu.wallet.chain.utils;

import com.google.common.base.Joiner;
import com.jiu.wallet.btc.utils.NumericUtil;
import com.jiu.wallet.chain.ChainType;
import org.bitcoinj.crypto.MnemonicCode;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

public class MnemonicUtil {
  public static void validateMnemonics(List<String> mnemonicCodes) throws Exception {
    try {
      MnemonicCode.INSTANCE.check(mnemonicCodes);
    } catch (org.bitcoinj.crypto.MnemonicException.MnemonicLengthException e) {
      throw new Exception("mnemonic_length_invalid");
    } catch (org.bitcoinj.crypto.MnemonicException.MnemonicWordException e) {
      throw new Exception("mnemonic_word_invalid");
    } catch (Exception e) {
      throw new Exception("mnemonic_checksum_invalid");
    }
  }

  public static List<String> randomMnemonicCodes() throws Exception {
    return toMnemonicCodes(NumericUtil.generateRandomBytes(16));
  }

  public static String randomMnemonicStr() throws Exception {
    List<String> mnemonicCodes=randomMnemonicCodes();
    return Joiner.on(" ").join(mnemonicCodes);
  }

  public static void main(String[] args) throws Exception {
    AddressUtils addressUtils = new AddressUtils();
    addressUtils.setMnemonicCodes(randomMnemonicCodes());
    BIP44Util.Metadata metadata = new BIP44Util.Metadata();
    metadata.setChainType(ChainType.BITCOIN);
    metadata.setMainNet(true);
    metadata.setSegWit("P2WPKH");
    addressUtils.setMetadata(metadata);
    addressUtils.btcAddress();

  }


  public static List<String> toMnemonicCodes(byte[] entropy) throws Exception {
    try {
      return MnemonicCode.INSTANCE.toMnemonic(entropy);
    } catch (org.bitcoinj.crypto.MnemonicException.MnemonicLengthException e) {
      throw new Exception("mnemonic_length_invalid");
    } catch (Exception e) {
      throw new Exception("mnemonic_checksum_invalid");
    }
  }

}