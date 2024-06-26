package com.jiu.wallet.eth;



import com.jiu.wallet.btc.utils.ByteUtil;
import com.jiu.wallet.btc.utils.Hash;
import com.jiu.wallet.btc.utils.NumericUtil;
import com.jiu.wallet.chain.utils.EthereumSignUtil;
import com.jiu.wallet.chain.utils.SignatureData;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Transaction class used for signing transactions locally.<br>
 * For the specification, refer to p4 of the <a href="http://gavwood.com/paper.pdf">
 * <p>
 * yellow paper</a>.
 */
public class EthereumTransaction{

  private BigInteger nonce;
  private BigInteger gasPrice;
  private BigInteger gasLimit;
  private String to;
  private BigInteger value;
  private String data;

  public EthereumTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
                             BigInteger value, String data) {
    this.nonce = nonce;
    this.gasPrice = gasPrice;
    this.gasLimit = gasLimit;
    this.to = to;
    this.value = value;

    if (data != null) {
      this.data = NumericUtil.cleanHexPrefix(data);
    }
  }

  public BigInteger getNonce() {
    return nonce;
  }

  public BigInteger getGasPrice() {
    return gasPrice;
  }

  public BigInteger getGasLimit() {
    return gasLimit;
  }

  public String getTo() {
    return to;
  }

  public BigInteger getValue() {
    return value;
  }

  public String getData() {
    return data;
  }

  public String signTransaction(String chainID,byte[] privateKey) {

    String signedTx = signTransaction(Integer.parseInt(chainID), privateKey);
    String txHash = this.calcTxHash(signedTx);
    return txHash;
  }

  String signTransaction(int chainId, byte[] privateKey) {
    SignatureData signatureData = new SignatureData(chainId, new byte[]{}, new byte[]{});
    byte[] encodedTransaction = encodeToRLP(signatureData);
    signatureData = EthereumSignUtil.signMessage(encodedTransaction, privateKey);
    SignatureData eip155SignatureData = createEip155SignatureData(signatureData, chainId);
    byte[] rawSignedTx = encodeToRLP(eip155SignatureData);
    return NumericUtil.bytesToHex(rawSignedTx);
  }

  String calcTxHash(String signedTx) {
    return NumericUtil.prependHexPrefix(Hash.keccak256(signedTx));
  }

  private static SignatureData createEip155SignatureData(SignatureData signatureData, int chainId) {
    int v = signatureData.getV() + (chainId * 2) + 8;

    return new SignatureData(v, signatureData.getR(), signatureData.getS());
  }

  byte[] encodeToRLP(SignatureData signatureData) {
    List<RlpType> values = asRlpValues(signatureData);
    RlpList rlpList = new RlpList(values);
    return RlpEncoder.encode(rlpList);
  }

  List<RlpType> asRlpValues(SignatureData signatureData) {
    List<RlpType> result = new ArrayList<>();

    result.add(RlpString.create(getNonce()));
    result.add(RlpString.create(getGasPrice()));
    result.add(RlpString.create(getGasLimit()));

    // an empty to address (contract creation) should not be encoded as a numeric 0 value
    String to = getTo();
    if (to != null && to.length() > 0) {
      // addresses that start with zeros should be encoded with the zeros included, not
      // as numeric values
      result.add(RlpString.create(NumericUtil.hexToBytes(to)));
    } else {
      result.add(RlpString.create(""));
    }

    result.add(RlpString.create(getValue()));

    // value field will already be hex encoded, so we need to convert into binary first
    byte[] data = NumericUtil.hexToBytes(getData());
    result.add(RlpString.create(data));

    if (signatureData != null && signatureData.getV() > 0) {
      result.add(RlpString.create(signatureData.getV()));
      result.add(RlpString.create(ByteUtil.trimLeadingZeroes(signatureData.getR())));
      result.add(RlpString.create(ByteUtil.trimLeadingZeroes(signatureData.getS())));
    }

    return result;
  }
}
