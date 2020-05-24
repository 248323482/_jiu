package com.jiu.wallet.btc.rpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;

@Data
public class BitcoinRPCClient
        implements Bitcoin {
    public final URL rpcURL;
    private URL noAuthURL;
    private String authStr;
    private HostnameVerifier hostnameVerifier;
    private SSLSocketFactory sslSocketFactory;
    private int connectTimeout;
    public static final Charset QUERY_CHARSET = Charset.forName("UTF-8");

    public BitcoinRPCClient(String rpcUrl)
            throws MalformedURLException {
        rpcURL = new URL(rpcUrl);
    }

    public BitcoinRPCClient(URL rpc) {
        hostnameVerifier = null;
        sslSocketFactory = null;
        connectTimeout = 0;
        rpcURL = rpc;
        try {
            noAuthURL = (new URI(rpc.getProtocol(), null, rpc.getHost(), rpc.getPort(), rpc.getPath(), rpc.getQuery(), null)).toURL();
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(rpc.toString(), ex);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(rpc.toString(), ex);
        }
        authStr = rpc.getUserInfo() != null ? String.valueOf(Base64Coder.encode(rpc.getUserInfo().getBytes(Charset.forName("ISO8859-1")))) : null;
    }


    public byte[] prepareRequest(final String method, final Object params[]) {
        return JSON.toJSONString(new LinkedHashMap() {
            {
                put("method", method);
                put("params", ((Object) (params)));
                put("id", "1");
            }
        }).getBytes(QUERY_CHARSET);
    }


    private static byte[] loadStream(InputStream in, boolean close)
            throws IOException {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        byte buffer[] = new byte[1024];
        do {
            int nr = in.read(buffer);
            if (nr != -1) {
                if (nr == 0)
                    throw new IOException("Read timed out");
                o.write(buffer, 0, nr);
            } else {
                return o.toByteArray();
            }
        } while (true);
    }

    public Object loadResponse(InputStream in, Object expectedID, boolean close)
            throws IOException, Exception {
        String r;
        r = new String(loadStream(in, close), QUERY_CHARSET);
        Object obj;
        try {
            JSONObject response = com.alibaba.fastjson.JSON.parseObject(r);
            if (!expectedID.equals(response.get("id")))
                throw new Exception((new StringBuilder()).append("Wrong response ID (expected: ").append(String.valueOf(expectedID)).append(", response: ").append(response.get("id")).append(")").toString());
            if (response.get("error") != null)
                throw new Exception(JSON.toJSONString(response.get("error")));
            obj = response.get("result");
        } catch (ClassCastException ex) {
            throw new Exception((new StringBuilder()).append("Invalid server response format (data: \"").append(r).append("\")").toString());
        }
        if (close)
            in.close();
        return obj;

    }


    public Object query(String method, Object o[])
            throws Exception {
        HttpURLConnection conn;
        conn = (HttpURLConnection) noAuthURL.openConnection();
        if (connectTimeout != 0)
            conn.setConnectTimeout(connectTimeout);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        if (conn instanceof HttpsURLConnection) {
            if (hostnameVerifier != null)
                ((HttpsURLConnection) conn).setHostnameVerifier(hostnameVerifier);
            if (sslSocketFactory != null)
                ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
        }
        conn.setRequestProperty("Authorization", (new StringBuilder()).append("Basic ").append(authStr).toString());
        byte r[] = prepareRequest(method, o);
        conn.getOutputStream().write(r);
        conn.getOutputStream().close();
        int responseCode = conn.getResponseCode();
        if (responseCode != 200)
            throw new Exception((new StringBuilder()).append("RPC Query Failed (method: ").append(method).append(", params: ").append(Arrays.deepToString(o)).append(", response header: ").append(responseCode).append(" ").append(conn.getResponseMessage()).append(", response: ").append(new String(loadStream(conn.getErrorStream(), true))).toString());
        return loadResponse(conn.getInputStream(), "1", true);
    }

    private class RawTransactionImpl extends MapWrapper
            implements Bitcoin.RawTransaction {
        public RawTransactionImpl(Map m) {
            super(m);
        }

        private class OutImpl extends MapWrapper
                implements Bitcoin.RawTransaction.Out {
            private class ScriptPubKeyImpl extends MapWrapper
                    implements Bitcoin.RawTransaction.Out.ScriptPubKey {

                public String asm() {
                    return mapStr("asm");
                }

                public String hex() {
                    return mapStr("hex");
                }

                public int reqSigs() {
                    return mapInt("reqSigs");
                }

                public String type() {
                    return mapStr(type());
                }

                public List addresses() {
                    return (List) m.get("addresses");
                }

                public ScriptPubKeyImpl(Map m) {
                    super(m);
                }
            }


            public double value() {
                return mapDouble("value");
            }

            public int n() {
                return mapInt("n");
            }

            public Bitcoin.RawTransaction.Out.ScriptPubKey scriptPubKey() {
                return new ScriptPubKeyImpl((Map) m.get("scriptPubKey"));
            }

            public Bitcoin.TxInput toInput() {
                return new Bitcoin.BasicTxInput(transaction().txId(), n());
            }

            public Bitcoin.RawTransaction transaction() {
                return RawTransactionImpl.this;
            }

            public OutImpl(Map m) {
                super(m);
            }
        }

        private class InImpl extends MapWrapper
                implements Bitcoin.RawTransaction.In {


            public String txid() {
                return mapStr("txid");
            }

            public int vout() {
                return mapInt("vout");
            }

            public Map scriptSig() {
                return (Map) m.get("scriptSig");
            }

            public long sequence() {
                return mapLong("sequence");
            }

            public Bitcoin.RawTransaction getTransaction() throws Exception {
                return getRawTransaction(mapStr("txid"));

            }

            public Bitcoin.RawTransaction.Out getTransactionOutput() throws Exception {
                return (Bitcoin.RawTransaction.Out) getTransaction().vOut().get(mapInt("vout"));
            }

            public InImpl(Map m) {
                super(m);
            }
        }


        public String hex() {
            return mapStr("hex");
        }

        public String txId() {
            return mapStr("txid");
        }

        public int version() {
            return mapInt("version");
        }

        public long lockTime() {
            return mapLong("locktime");
        }

        public List vIn() {
            final List vIn = (List) m.get("vin");
            return new AbstractList() {

                public int size() {
                    return vIn.size();
                }

                public Object get(int i) {
                    return get(i);
                }
            };
        }

        public List vOut() {
            final List vOut = (List) m.get("vout");
            return new AbstractList() {


                public int size() {
                    return vOut.size();
                }

                public Object get(int i) {
                    return get(i);
                }
            };
        }

        public String blockHash() {
            return mapStr("blockhash");
        }

        public int confirmations() {
            return mapInt("confirmations");
        }

        public Date time() {
            return mapCTime("time");
        }

        public Date blocktime() {
            return mapCTime("blocktime");
        }


    }


    private class UnspentListWrapper extends ListMapWrapper {


        protected Bitcoin.Unspent wrap(final Map m) {
            return new Bitcoin.Unspent() {
                public String txid() {
                    return MapWrapper.mapStr(m, "txid");
                }

                public int vout() {
                    return MapWrapper.mapInt(m, "vout");
                }

                public String address() {
                    return MapWrapper.mapStr(m, "address");
                }

                public String scriptPubKey() {
                    return MapWrapper.mapStr(m, "scriptPubKey");
                }

                public String account() {
                    return MapWrapper.mapStr(m, "account");
                }

                public BigDecimal amount() {
                    return MapWrapper.mapBigDecimal(m, "amount");
                }

                public int confirmations() {
                    return MapWrapper.mapInt(m, "confirmations");
                }
            };
        }

        public UnspentListWrapper(List list) {
            super(list);
        }
    }


    private class TransactionListMapWrapper extends ListMapWrapper {


        protected Bitcoin.Transaction wrap(final Map m) {
            return new Bitcoin.Transaction() {

                private Bitcoin.RawTransaction raw;

                public String account() {
                    return MapWrapper.mapStr(m, "account");
                }

                public String address() {
                    return MapWrapper.mapStr(m, "address");
                }

                public String category() {
                    return MapWrapper.mapStr(m, "category");
                }

                public double amount() {
                    return MapWrapper.mapDouble(m, "amount");
                }

                public double fee() {
                    return MapWrapper.mapDouble(m, "fee");
                }

                public int confirmations() {
                    return MapWrapper.mapInt(m, "confirmations");
                }

                public String blockHash() {
                    return MapWrapper.mapStr(m, "blockhash");
                }

                public int blockIndex() {
                    return MapWrapper.mapInt(m, "blockindex");
                }

                public Date blockTime() {
                    return MapWrapper.mapCTime(m, "blocktime");
                }

                public String txId() {
                    return MapWrapper.mapStr(m, "txid");
                }

                public Date time() {
                    return MapWrapper.mapCTime(m, "time");
                }

                public Date timeReceived() {
                    return MapWrapper.mapCTime(m, "timereceived");
                }

                public String comment() {
                    return MapWrapper.mapStr(m, "comment");
                }

                public String commentTo() {
                    return MapWrapper.mapStr(m, "to");
                }

                public Bitcoin.RawTransaction raw() {
                    try {
                        raw = getRawTransaction(txId());
                        if (raw == null) {
                            return raw;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return raw;

                }

                public String toString() {
                    return m.toString();
                }


            };
        }


        public TransactionListMapWrapper(List list) {
            super(list);
        }
    }


    private class BlockMapWrapper extends MapWrapper
            implements Bitcoin.Block {

        public String hash() {
            return mapStr("hash");
        }

        public int confirmations() {
            return mapInt("confirmations");
        }

        public int size() {
            return mapInt("size");
        }

        public int height() {
            return mapInt("height");
        }

        public int version() {
            return mapInt("version");
        }

        public String merkleRoot() {
            return mapStr("");
        }

        public List tx() {
            return (List) m.get("tx");
        }

        public Date time() {
            return mapCTime("time");
        }

        public long nonce() {
            return mapLong("nonce");
        }

        public String bits() {
            return mapStr("bits");
        }

        public double difficulty() {
            return mapDouble("difficulty");
        }

        public String previousHash() {
            return mapStr("previousblockhash");
        }

        public String nextHash() {
            return mapStr("nextblockhash");
        }

        public Bitcoin.Block previous()
                throws Exception {
            if (!m.containsKey("previousblockhash"))
                return null;
            else
                return getBlock(previousHash());
        }

        public Bitcoin.Block next()
                throws Exception {
            if (!m.containsKey("nextblockhash"))
                return null;
            else
                return getBlock(nextHash());
        }

        public BlockMapWrapper(Map m) {
            super(m);
        }
    }


    private class PeerInfoMapWrapper extends MapWrapper
            implements Bitcoin.PeerInfo {

        public String addr() {
            return mapStr("addr");
        }

        public String services() {
            return mapStr("services");
        }

        public int lastsend() {
            return mapInt("lastsend");
        }

        public int lastrecv() {
            return mapInt("lastrecv");
        }

        public int bytessent() {
            return mapInt("bytessent");
        }

        public int bytesrecv() {
            return mapInt("bytesrecv");
        }

        public int blocksrequested() {
            return mapInt("blocksrequested");
        }

        public Date conntime() {
            return mapCTime("conntime");
        }

        public int version() {
            return mapInt("version");
        }

        public String subver() {
            return mapStr("subver");
        }

        public boolean inbound() {
            return mapBool("inbound");
        }

        public int startingheight() {
            return mapInt("startingheight");
        }

        public int banscore() {
            return mapInt("banscore");
        }

        public PeerInfoMapWrapper(Map m) {
            super(m);
        }
    }


    private class MiningInfoMapWrapper extends MapWrapper
            implements Bitcoin.MiningInfo {


        public int blocks() {
            return mapInt("blocks");
        }

        public int currentblocksize() {
            return mapInt("currentblocksize");
        }

        public int currentblocktx() {
            return mapInt("currentblocktx");
        }

        public double difficulty() {
            return mapDouble("difficulty");
        }

        public String errors() {
            return mapStr("errors");
        }

        public int genproclimit() {
            return mapInt("genproclimit");
        }

        public double networkhashps() {
            return mapDouble("networkhashps");
        }

        public int pooledtx() {
            return mapInt("pooledtx");
        }

        public boolean testnet() {
            return mapBool("testnet");
        }

        public String chain() {
            return mapStr("chain");
        }

        public boolean generate() {
            return mapBool("generate");
        }

        public MiningInfoMapWrapper(Map m) {
            super(m);
        }
    }


    private class InfoMapWrapper extends MapWrapper
            implements Bitcoin.Info {

        public int version() {
            return mapInt("version");
        }

        public int protocolversion() {
            return mapInt("protocolversion");
        }

        public int walletversion() {
            return mapInt("walletversion");
        }

        public double balance() {
            return mapDouble("balance");
        }

        public int blocks() {
            return mapInt("blocks");
        }

        public int timeoffset() {
            return mapInt("timeoffset");
        }

        public int connections() {
            return mapInt("connections");
        }

        public String proxy() {
            return mapStr("proxy");
        }

        public double difficulty() {
            return mapDouble("difficulty");
        }

        public boolean testnet() {
            return mapBool("testnet");
        }

        public int keypoololdest() {
            return mapInt("keypoololdest");
        }

        public int keypoolsize() {
            return mapInt("keypoolsize");
        }

        public int unlocked_until() {
            return mapInt("unlocked_until");
        }

        public double paytxfee() {
            return mapDouble("paytxfee");
        }

        public double relayfee() {
            return mapDouble("relayfee");
        }

        public String errors() {
            return mapStr("errors");
        }

        public InfoMapWrapper(Map m) {
            super(m);
        }
    }


    public String createRawTransaction(List inputs, List outputs)
            throws Exception {
        List pInputs = new ArrayList();
        final Bitcoin.TxInput txInput;
        for (Iterator iterator = inputs.iterator(); iterator.hasNext(); pInputs.add(new LinkedHashMap() {
            final Bitcoin.TxInput txInput;

            {
                txInput = (Bitcoin.TxInput) iterator.next();
                put("txid", txInput.txid());
                put("vout", Integer.valueOf(txInput.vout()));
            }
        }))
            ;

        Map pOutputs = new LinkedHashMap();
        Iterator iterator1 = outputs.iterator();
        do {
            if (!iterator1.hasNext())
                break;
            Bitcoin.TxOutput txOutput = (Bitcoin.TxOutput) iterator1.next();
            BigDecimal oldValue;
            if ((oldValue = (BigDecimal) pOutputs.put(txOutput.address(), txOutput.amount())) != null)
                pOutputs.put(txOutput.address(), oldValue.add(txOutput.amount()));
        } while (true);
        return (String) query("createrawtransaction", new Object[]{
                pInputs, pOutputs
        });
    }

    @Override
    public RawTransaction decodeRawTransaction(String hex) throws Exception {
        return new RawTransactionImpl((Map) query("decoderawtransaction", new Object[]{
                hex
        }));
    }

    public String dumpPrivKey(String address)
            throws Exception {
        return (String) query("dumpprivkey", new Object[]{
                address
        });
    }

    public String getAccount(String address)
            throws Exception {
        return (String) query("getaccount", new Object[]{
                address
        });
    }

    public String getAccountAddress(String account)
            throws Exception {
        return (String) query("getaccountaddress", new Object[]{
                account
        });
    }

    public List getAddressesByAccount(String account)
            throws Exception {
        return (List) query("getaddressesbyaccount", new Object[]{
                account
        });
    }

    public double getBalance()
            throws Exception {
        return ((Number) query("getbalance", new Object[0])).doubleValue();
    }

    public double getBalance(String account)
            throws Exception {
        return ((Number) query("getbalance", new Object[]{
                account
        })).doubleValue();
    }

    public double getBalance(String account, int minConf)
            throws Exception {
        return ((Number) query("getbalance", new Object[]{
                account, Integer.valueOf(minConf)
        })).doubleValue();
    }

    public Bitcoin.Block getBlock(String blockHash)
            throws Exception {
        return new BlockMapWrapper((Map) query("getblock", new Object[]{
                blockHash
        }));
    }

    public int getBlockCount()
            throws Exception {
        return ((Number) query("getblockcount", new Object[0])).intValue();
    }

    public String getBlockHash(Long blockId)
            throws Exception {
        return (String) query("getblockhash", new Object[]{
                Long.valueOf(blockId)
        });
    }

    public int getConnectionCount()
            throws Exception {
        return ((Number) query("getconnectioncount", new Object[0])).intValue();
    }

    public double getDifficulty()
            throws Exception {
        return ((Number) query("getdifficulty", new Object[0])).doubleValue();
    }

    public boolean getGenerate()
            throws Exception {
        return ((Boolean) query("getgenerate", new Object[0])).booleanValue();
    }

    public double getHashesPerSec()
            throws Exception {
        return ((Number) query("gethashespersec", new Object[0])).doubleValue();
    }

    public Bitcoin.Info getInfo()
            throws Exception {
        return new InfoMapWrapper((Map) query("getinfo", new Object[0]));
    }

    public Bitcoin.MiningInfo getMiningInfo()
            throws Exception {
        return new MiningInfoMapWrapper((Map) query("getmininginfo", new Object[0]));
    }

    public String getNewAddress()
            throws Exception {
        return (String) query("getnewaddress", new Object[0]);
    }

    public String getNewAddress(String account)
            throws Exception {
        return (String) query("getnewaddress", new Object[]{
                account
        });
    }

    public Bitcoin.PeerInfo getPeerInfo()
            throws Exception {
        return new PeerInfoMapWrapper((Map) query("getmininginfo", new Object[0]));
    }

    public String getRawTransactionHex(String txId)
            throws Exception {
        return (String) query("getrawtransaction", new Object[]{
                txId
        });
    }

    public Bitcoin.RawTransaction getRawTransaction(String txId)
            throws Exception {
        return new RawTransactionImpl((Map) query("getrawtransaction", new Object[]{
                txId, Integer.valueOf(1)
        }));
    }

    public double getReceivedByAccount(String account)
            throws Exception {
        return ((Number) query("getreceivedbyaccount", new Object[]{
                account
        })).doubleValue();
    }

    public double getReceivedByAccount(String account, int minConf)
            throws Exception {
        return ((Number) query("getreceivedbyaccount", new Object[]{
                account, Integer.valueOf(minConf)
        })).doubleValue();
    }

    public double getReceivedByAddress(String address)
            throws Exception {
        return ((Number) query("getreceivedbyaddress", new Object[]{
                address
        })).doubleValue();
    }

    public double getReceivedByAddress(String address, int minConf)
            throws Exception {
        return ((Number) query("getreceivedbyaddress", new Object[]{
                address, Integer.valueOf(minConf)
        })).doubleValue();
    }

    public Bitcoin.RawTransaction getTransaction(String txId)
            throws Exception {
        return new RawTransactionImpl((Map) query("gettransaction", new Object[]{
                txId
        }));
    }

    public Bitcoin.TxOutSetInfo getTxOutSetInfo()
            throws Exception {
        final Map txoutsetinfoResult = (Map) query("gettxoutsetinfo", new Object[0]);
        return new Bitcoin.TxOutSetInfo() {
            public int height() {
                return ((Number) txoutsetinfoResult.get("height")).intValue();
            }

            public String bestBlock() {
                return (String) txoutsetinfoResult.get("bestblock");
            }

            public int transactions() {
                return ((Number) txoutsetinfoResult.get("transactions")).intValue();
            }

            public int txOuts() {
                return ((Number) txoutsetinfoResult.get("txouts")).intValue();
            }

            public int bytesSerialized() {
                return ((Number) txoutsetinfoResult.get("bytes_serialized")).intValue();
            }

            public String hashSerialized() {
                return (String) txoutsetinfoResult.get("hash_serialized");
            }

            public double totalAmount() {
                return ((Number) txoutsetinfoResult.get("total_amount")).doubleValue();
            }

            public String toString() {
                return txoutsetinfoResult.toString();
            }
        };
    }


    private static class ReceivedAddressListWrapper extends AbstractList {

        private final List wrappedList;

        public Bitcoin.ReceivedAddress get(int index) {
            final Map e = (Map) wrappedList.get(index);
            return new Bitcoin.ReceivedAddress() {
                public String address() {
                    return (String) e.get("address");
                }

                public String account() {
                    return (String) e.get("account");
                }

                public double amount() {
                    return ((Number) e.get("amount")).doubleValue();
                }

                public int confirmations() {
                    return ((Number) e.get("confirmations")).intValue();
                }

                public String toString() {
                    return e.toString();
                }


            };
        }

        public int size() {
            return wrappedList.size();
        }

        public ReceivedAddressListWrapper(List wrappedList) {
            this.wrappedList = wrappedList;
        }
    }

    public Bitcoin.Work getWork()
            throws Exception {
        final Map workResult = (Map) query("getwork", new Object[0]);
        return new Bitcoin.Work() {
            public String midstate() {
                return (String) workResult.get("midstate");
            }

            public String data() {
                return (String) workResult.get("data");
            }

            public String hash1() {
                return (String) workResult.get("hash1");
            }

            public String target() {
                return (String) workResult.get("target");
            }

            public String toString() {
                return workResult.toString();
            }
        };
    }

    public void importPrivKey(String bitcoinPrivKey)
            throws Exception {
        query("importprivkey", new Object[]{
                bitcoinPrivKey
        });
    }

    public void importPrivKey(String bitcoinPrivKey, String label)
            throws Exception {
        query("importprivkey", new Object[]{
                bitcoinPrivKey, label
        });
    }

    public void importPrivKey(String bitcoinPrivKey, String label, boolean rescan)
            throws Exception {
        query("importprivkey", new Object[]{
                bitcoinPrivKey, label, Boolean.valueOf(rescan)
        });
    }

    public Map listAccounts()
            throws Exception {
        return (Map) query("listaccounts", new Object[0]);
    }

    public Map listAccounts(int minConf)
            throws Exception {
        return (Map) query("listaccounts", new Object[]{
                Integer.valueOf(minConf)
        });
    }

    public List listReceivedByAccount()
            throws Exception {
        return new ReceivedAddressListWrapper((List) query("listreceivedbyaccount", new Object[0]));
    }

    public List listReceivedByAccount(int minConf)
            throws Exception {
        return new ReceivedAddressListWrapper((List) query("listreceivedbyaccount", new Object[]{
                Integer.valueOf(minConf)
        }));
    }

    public List listReceivedByAccount(int minConf, boolean includeEmpty)
            throws Exception {
        return new ReceivedAddressListWrapper((List) query("listreceivedbyaccount", new Object[]{
                Integer.valueOf(minConf), Boolean.valueOf(includeEmpty)
        }));
    }

    public List listReceivedByAddress()
            throws Exception {
        return new ReceivedAddressListWrapper((List) query("listreceivedbyaddress", new Object[0]));
    }

    public List listReceivedByAddress(int minConf)
            throws Exception {
        return new ReceivedAddressListWrapper((List) query("listreceivedbyaddress", new Object[]{
                Integer.valueOf(minConf)
        }));
    }

    public List listReceivedByAddress(int minConf, boolean includeEmpty)
            throws Exception {
        return new ReceivedAddressListWrapper((List) query("listreceivedbyaddress", new Object[]{
                Integer.valueOf(minConf), Boolean.valueOf(includeEmpty)
        }));
    }


    private class TransactionsSinceBlockImpl
            implements Bitcoin.TransactionsSinceBlock {

        public final List transactions;
        public final String lastBlock;

        public List transactions() {
            return transactions;
        }

        public String lastBlock() {
            return lastBlock;
        }

        public TransactionsSinceBlockImpl(Map r) {
            transactions = new TransactionListMapWrapper((List) r.get("transactions"));
            lastBlock = (String) r.get("lastblock");
        }
    }

    public Bitcoin.TransactionsSinceBlock listSinceBlock()
            throws Exception {
        return new TransactionsSinceBlockImpl((Map) query("listsinceblock", new Object[0]));
    }

    public Bitcoin.TransactionsSinceBlock listSinceBlock(String blockHash)
            throws Exception {
        return new TransactionsSinceBlockImpl((Map) query("listsinceblock", new Object[]{
                blockHash
        }));
    }

    public Bitcoin.TransactionsSinceBlock listSinceBlock(String blockHash, int targetConfirmations)
            throws Exception {
        return new TransactionsSinceBlockImpl((Map) query("listsinceblock", new Object[]{
                blockHash, Integer.valueOf(targetConfirmations)
        }));
    }

    public List listTransactions()
            throws Exception {
        return new TransactionListMapWrapper((List) query("listtransactions", new Object[0]));
    }

    public List listTransactions(String account)
            throws Exception {
        return new TransactionListMapWrapper((List) query("listtransactions", new Object[]{
                account
        }));
    }

    public List listTransactions(String account, int count)
            throws Exception {
        return new TransactionListMapWrapper((List) query("listtransactions", new Object[]{
                account, Integer.valueOf(count)
        }));
    }

    public List listTransactions(String account, int count, int from)
            throws Exception {
        return new TransactionListMapWrapper((List) query("listtransactions", new Object[]{
                account, Integer.valueOf(count), Integer.valueOf(from)
        }));
    }

    public List listUnspent()
            throws Exception {
        return new UnspentListWrapper((List) query("listunspent", new Object[0]));
    }

    public List listUnspent(int minConf)
            throws Exception {
        return new UnspentListWrapper((List) query("listunspent", new Object[]{
                Integer.valueOf(minConf)
        }));
    }

    public List listUnspent(int minConf, int maxConf)
            throws Exception {
        return new UnspentListWrapper((List) query("listunspent", new Object[]{
                Integer.valueOf(minConf), Integer.valueOf(maxConf)
        }));
    }

    public List listUnspent(int minConf, int maxConf, String addresses[])
            throws Exception {
        return new UnspentListWrapper((List) query("listunspent", new Object[]{
                Integer.valueOf(minConf), Integer.valueOf(maxConf), addresses
        }));
    }

    public String sendFrom(String fromAccount, String toBitcoinAddress, double amount)
            throws Exception {
        return (String) query("sendfrom", new Object[]{
                fromAccount, toBitcoinAddress, Double.valueOf(amount)
        });
    }

    public String sendFrom(String fromAccount, String toBitcoinAddress, double amount, int minConf)
            throws Exception {
        return (String) query("sendfrom", new Object[]{
                fromAccount, toBitcoinAddress, Double.valueOf(amount), Integer.valueOf(minConf)
        });
    }

    public String sendFrom(String fromAccount, String toBitcoinAddress, double amount, int minConf, String comment)
            throws Exception {
        return (String) query("sendfrom", new Object[]{
                fromAccount, toBitcoinAddress, Double.valueOf(amount), Integer.valueOf(minConf), comment
        });
    }

    public String sendFrom(String fromAccount, String toBitcoinAddress, double amount, int minConf, String comment, String commentTo)
            throws Exception {
        return (String) query("sendfrom", new Object[]{
                fromAccount, toBitcoinAddress, Double.valueOf(amount), Integer.valueOf(minConf), comment, commentTo
        });
    }

    public String sendMany(String fromAccount, List outputs)
            throws Exception {
        Map pOutputs = new LinkedHashMap();
        Iterator iterator = outputs.iterator();
        do {
            if (!iterator.hasNext())
                break;
            Bitcoin.TxOutput txOutput = (Bitcoin.TxOutput) iterator.next();
            BigDecimal oldValue;
            if ((oldValue = (BigDecimal) pOutputs.put(txOutput.address(), txOutput.amount())) != null)
                pOutputs.put(txOutput.address(), oldValue.add(txOutput.amount()));
        } while (true);
        return (String) query("sendmany", new Object[]{
                fromAccount, pOutputs
        });
    }

    public String sendMany(String fromAccount, List outputs, int minConf)
            throws Exception {
        Map pOutputs = new LinkedHashMap();
        Iterator iterator = outputs.iterator();
        do {
            if (!iterator.hasNext())
                break;
            Bitcoin.TxOutput txOutput = (Bitcoin.TxOutput) iterator.next();
            BigDecimal oldValue;
            if ((oldValue = (BigDecimal) pOutputs.put(txOutput.address(), txOutput.amount())) != null)
                pOutputs.put(txOutput.address(), oldValue.add(txOutput.amount()));
        } while (true);
        return (String) query("sendmany", new Object[]{
                fromAccount, pOutputs, Integer.valueOf(minConf)
        });
    }

    public String sendMany(String fromAccount, List outputs, int minConf, String comment)
            throws Exception {
        Map pOutputs = new LinkedHashMap();
        Iterator iterator = outputs.iterator();
        do {
            if (!iterator.hasNext())
                break;
            Bitcoin.TxOutput txOutput = (Bitcoin.TxOutput) iterator.next();
            BigDecimal oldValue;
            if ((oldValue = (BigDecimal) pOutputs.put(txOutput.address(), txOutput.amount())) != null)
                pOutputs.put(txOutput.address(), oldValue.add(txOutput.amount()));
        } while (true);
        return (String) query("sendmany", new Object[]{
                fromAccount, pOutputs, Integer.valueOf(minConf), comment
        });
    }

    public String sendRawTransaction(String hex)
            throws Exception {
        return (String) query("sendrawtransaction", new Object[]{
                hex
        });
    }

    public String sendToAddress(String toAddress, double amount)
            throws Exception {
        return (String) query("sendtoaddress", new Object[]{
                toAddress, Double.valueOf(amount)
        });
    }

    public String sendToAddress(String toAddress, double amount, String comment)
            throws Exception {
        return (String) query("sendtoaddress", new Object[]{
                toAddress, Double.valueOf(amount), comment
        });
    }

    public Boolean setTxFee(double amount)
            throws Exception {
        return (Boolean) query("settxfee", new Object[]{
                Double.valueOf(amount)
        });
    }

    @Override
    public String sendToAddress(String toAddress, double amount, String comment, String commentTo)
            throws Exception {
        return (String) query("sendtoaddress", new Object[]{
                toAddress, Double.valueOf(amount), comment, commentTo
        });
    }

    @Override
    public String signMessage(String address, String message) throws Exception {
        return (String) query("signmessage", new Object[]{
                address, message
        });
    }

    @Override
    public String signRawTransaction(String hex) throws Exception {
        Map result = (Map) query("signrawtransaction", new Object[]{
                hex
        });
        if (((Boolean) result.get("complete")).booleanValue())
            return (String) result.get("hex");
        else
            throw new Exception("Incomplete");
    }

    @Override
    public void stop()
            throws Exception {
        query("stop", new Object[0]);
    }

    @Override
    public Bitcoin.AddressValidationResult validateAddress(String address)
            throws Exception {
        final Map validationResult = (Map) query("validateaddress", new Object[]{
                address
        });
        return new Bitcoin.AddressValidationResult() {
            public boolean isValid() {
                return ((Boolean) validationResult.get("isvalid")).booleanValue();
            }

            public String address() {
                return (String) validationResult.get("address");
            }

            public boolean isMine() {
                return ((Boolean) validationResult.get("ismine")).booleanValue();
            }

            public boolean isScript() {
                return ((Boolean) validationResult.get("isscript")).booleanValue();
            }

            public String pubKey() {
                return (String) validationResult.get("pubkey");
            }

            public boolean isCompressed() {
                return ((Boolean) validationResult.get("iscompressed")).booleanValue();
            }

            public String account() {
                return (String) validationResult.get("account");
            }

            public String toString() {
                return validationResult.toString();
            }
        };
    }

    @Override
    public boolean verifyMessage(String address, String signature, String message)
            throws Exception {
        return ((Boolean) query("verifymessage", new Object[]{
                address, signature, message
        })).booleanValue();
    }
}
