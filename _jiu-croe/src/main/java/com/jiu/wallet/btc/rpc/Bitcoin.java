

package com.jiu.wallet.btc.rpc;

import java.math.BigDecimal;
import java.util.*;


public interface Bitcoin {
    public static interface AddressValidationResult {

        public abstract boolean isValid();

        public abstract String address();

        public abstract boolean isMine();

        public abstract boolean isScript();

        public abstract String pubKey();

        public abstract boolean isCompressed();

        public abstract String account();
    }

    public static interface Unspent
            extends TxInput, TxOutput {

        public abstract String txid();

        public abstract int vout();

        public abstract String address();

        public abstract String account();

        public abstract String scriptPubKey();

        public abstract BigDecimal amount();

        public abstract int confirmations();
    }

    public static interface TransactionsSinceBlock {

        public abstract List transactions();

        public abstract String lastBlock();
    }

    public static interface Transaction {

        public abstract String account();

        public abstract String address();

        public abstract String category();

        public abstract double amount();

        public abstract double fee();

        public abstract int confirmations();

        public abstract String blockHash();

        public abstract int blockIndex();

        public abstract Date blockTime();

        public abstract String txId();

        public abstract Date time();

        public abstract Date timeReceived();

        public abstract String comment();

        public abstract String commentTo();

        public abstract RawTransaction raw();
    }

    public static interface ReceivedAddress {

        public abstract String address();

        public abstract String account();

        public abstract double amount();

        public abstract int confirmations();
    }

    public static interface Work {

        public abstract String midstate();

        public abstract String data();

        public abstract String hash1();

        public abstract String target();
    }

    public static interface TxOutSetInfo {

        public abstract int height();

        public abstract String bestBlock();

        public abstract int transactions();

        public abstract int txOuts();

        public abstract int bytesSerialized();

        public abstract String hashSerialized();

        public abstract double totalAmount();
    }

    public static interface RawTransaction {
        public static interface Out {
            public static interface ScriptPubKey {

                public abstract String asm();

                public abstract String hex();

                public abstract int reqSigs();

                public abstract String type();

                public abstract List addresses();
            }


            public abstract double value();

            public abstract int n();

            public abstract ScriptPubKey scriptPubKey();

            public abstract TxInput toInput();

            public abstract RawTransaction transaction();
        }

        public static interface In
                extends TxInput {

            public abstract Map scriptSig();

            public abstract long sequence();

            public abstract RawTransaction getTransaction() throws Exception;

            public abstract Out getTransactionOutput() throws Exception;
        }


        public abstract String hex();

        public abstract String txId();

        public abstract int version();

        public abstract long lockTime();

        public abstract List vIn();

        public abstract List vOut();

        public abstract String blockHash();

        public abstract int confirmations();

        public abstract Date time();

        public abstract Date blocktime();
    }

    public static interface PeerInfo {

        public abstract String addr();

        public abstract String services();

        public abstract int lastsend();

        public abstract int lastrecv();

        public abstract int bytessent();

        public abstract int bytesrecv();

        public abstract int blocksrequested();

        public abstract Date conntime();

        public abstract int version();

        public abstract String subver();

        public abstract boolean inbound();

        public abstract int startingheight();

        public abstract int banscore();
    }

    public static interface MiningInfo {

        public abstract int blocks();

        public abstract int currentblocksize();

        public abstract int currentblocktx();

        public abstract double difficulty();

        public abstract String errors();

        public abstract int genproclimit();

        public abstract double networkhashps();

        public abstract int pooledtx();

        public abstract boolean testnet();

        public abstract String chain();

        public abstract boolean generate();
    }

    public static interface Info {

        public abstract int version();

        public abstract int protocolversion();

        public abstract int walletversion();

        public abstract double balance();

        public abstract int blocks();

        public abstract int timeoffset();

        public abstract int connections();

        public abstract String proxy();

        public abstract double difficulty();

        public abstract boolean testnet();

        public abstract int keypoololdest();

        public abstract int keypoolsize();

        public abstract int unlocked_until();

        public abstract double paytxfee();

        public abstract double relayfee();

        public abstract String errors();
    }

    public static interface Block {

        public abstract String hash();

        public abstract int confirmations();

        public abstract int size();

        public abstract int height();

        public abstract int version();

        public abstract String merkleRoot();

        public abstract List tx();

        public abstract Date time();

        public abstract long nonce();

        public abstract String bits();

        public abstract double difficulty();

        public abstract String previousHash();

        public abstract String nextHash();

        public abstract Block previous()
                throws Exception;

        public abstract Block next()
                throws Exception;
    }

    public static class BasicTxOutput
            implements TxOutput {

        public String address;
        public BigDecimal amount;

        public String address() {
            return address;
        }

        public BigDecimal amount() {
            return amount;
        }

        public BasicTxOutput(String address, BigDecimal amount) {
            this.address = address;
            this.amount = amount;
        }
    }

    public static interface TxOutput {

        public abstract String address();

        public abstract BigDecimal amount();
    }

    public static class BasicTxInput
            implements TxInput {

        public String txid;
        public int vout;

        public String txid() {
            return txid;
        }

        public int vout() {
            return vout;
        }

        public BasicTxInput(String txid, int vout) {
            this.txid = txid;
            this.vout = vout;
        }
    }

    public static interface TxInput {

        public abstract String txid();

        public abstract int vout();
    }



    public abstract String createRawTransaction(List list, List list1)
            throws Exception;

    public abstract RawTransaction decodeRawTransaction(String s)
            throws Exception;

    public abstract String dumpPrivKey(String s)
            throws Exception;

    public abstract String getAccount(String s)
            throws Exception;

    public abstract String getAccountAddress(String s)
            throws Exception;

    public abstract List getAddressesByAccount(String s)
            throws Exception;

    public abstract double getBalance()
            throws Exception;

    public abstract double getBalance(String s)
            throws Exception;

    public abstract double getBalance(String s, int i)
            throws Exception;

    public abstract Block getBlock(String s)
            throws Exception;

    public abstract int getBlockCount()
            throws Exception;

    public abstract String getBlockHash(Long i)
            throws Exception;

    public abstract int getConnectionCount()
            throws Exception;

    public abstract double getDifficulty()
            throws Exception;

    public abstract boolean getGenerate()
            throws Exception;

    public abstract double getHashesPerSec()
            throws Exception;

    public abstract Info getInfo()
            throws Exception;

    public abstract MiningInfo getMiningInfo()
            throws Exception;

    public abstract String getNewAddress()
            throws Exception;

    public abstract String getNewAddress(String s)
            throws Exception;

    public abstract PeerInfo getPeerInfo()
            throws Exception;

    public abstract String getRawTransactionHex(String s)
            throws Exception;

    public abstract RawTransaction getRawTransaction(String s)
            throws Exception;

    public abstract double getReceivedByAccount(String s)
            throws Exception;

    public abstract double getReceivedByAccount(String s, int i)
            throws Exception;

    public abstract double getReceivedByAddress(String s)
            throws Exception;

    public abstract double getReceivedByAddress(String s, int i)
            throws Exception;

    public abstract RawTransaction getTransaction(String s)
            throws Exception;

    public abstract TxOutSetInfo getTxOutSetInfo()
            throws Exception;

    public abstract Work getWork()
            throws Exception;

    public abstract void importPrivKey(String s)
            throws Exception;

    public abstract void importPrivKey(String s, String s1)
            throws Exception;

    public abstract void importPrivKey(String s, String s1, boolean flag)
            throws Exception;

    public abstract Map listAccounts()
            throws Exception;

    public abstract Map listAccounts(int i)
            throws Exception;

    public abstract List listReceivedByAccount()
            throws Exception;

    public abstract List listReceivedByAccount(int i)
            throws Exception;

    public abstract List listReceivedByAccount(int i, boolean flag)
            throws Exception;

    public abstract List listReceivedByAddress()
            throws Exception;

    public abstract List listReceivedByAddress(int i)
            throws Exception;

    public abstract List listReceivedByAddress(int i, boolean flag)
            throws Exception;

    public abstract TransactionsSinceBlock listSinceBlock()
            throws Exception;

    public abstract TransactionsSinceBlock listSinceBlock(String s)
            throws Exception;

    public abstract TransactionsSinceBlock listSinceBlock(String s, int i)
            throws Exception;

    public abstract List listTransactions()
            throws Exception;

    public abstract List listTransactions(String s)
            throws Exception;

    public abstract List listTransactions(String s, int i)
            throws Exception;

    public abstract List listTransactions(String s, int i, int j)
            throws Exception;

    public abstract List listUnspent()
            throws Exception;

    public abstract List listUnspent(int i)
            throws Exception;

    public abstract List listUnspent(int i, int j)
            throws Exception;

    public  abstract List listUnspent(int i, int j, String as[])
            throws Exception;

    public abstract String sendFrom(String s, String s1, double d)
            throws Exception;

    public abstract String sendFrom(String s, String s1, double d, int i)
            throws Exception;

    public abstract String sendFrom(String s, String s1, double d, int i, String s2)
            throws Exception;

    public abstract String sendFrom(String s, String s1, double d, int i, String s2, String s3)
            throws Exception;

    public abstract String sendMany(String s, List list)
            throws Exception;

    public abstract String sendMany(String s, List list, int i)
            throws Exception;

    public abstract String sendMany(String s, List list, int i, String s1)
            throws Exception;

    public abstract String sendRawTransaction(String s)
            throws Exception;

    public abstract String sendToAddress(String s, double d)
            throws Exception;

    public abstract String sendToAddress(String s, double d, String s1)
            throws Exception;

    public abstract Boolean setTxFee(double d)
            throws Exception;

    public abstract String sendToAddress(String s, double d, String s1, String s2)
            throws Exception;

    public abstract String signMessage(String s, String s1)
            throws Exception;

    public abstract String signRawTransaction(String s)
            throws Exception;

    public abstract void stop()
            throws Exception;

    public abstract AddressValidationResult validateAddress(String s)
            throws Exception;

    public abstract boolean verifyMessage(String s, String s1, String s2)
            throws Exception;
}
