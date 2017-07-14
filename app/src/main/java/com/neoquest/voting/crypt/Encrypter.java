package com.neoquest.voting.crypt;

import android.util.Base64;
import android.util.Log;

import com.neoquest.voting.model.entity.VoteRequest;

import java.io.UnsupportedEncodingException;

public class Encrypter {

    static {
        System.loadLibrary("native-lib");
    }

    public VoteRequest encrypt(String hashValue, String publicKey, String k) throws UnsupportedEncodingException {
        final char[] msgChar    = hashValue.toCharArray();
        final char[] pubKeyChar = new String(Base64.decode(publicKey.getBytes("UTF-8"), Base64.DEFAULT)).toCharArray();
        final char[] kChar      = new String(Base64.decode(k.getBytes("UTF-8"), Base64.DEFAULT)).toCharArray();
        final char[] images     = new char[768];
        encrypt(pubKeyChar, kChar, msgChar, images);
        VoteRequest voteRequest = new VoteRequest();
        voteRequest.setCipher(Base64.encodeToString(new String(msgChar).getBytes("UTF-8"), Base64.DEFAULT));
        voteRequest.setImage(Base64.encodeToString(new String(images).getBytes("UTF-8"), Base64.DEFAULT));
        return voteRequest;
    }

    public native void keyGen(char[] k, char[] pub, char[] priv);
    public native void encrypt(char[] publicKey, char[] k, char[] message, char[] images);
    public native void decrypt(char[] privateKey, char[] k, char[] message, char[] images);
}
