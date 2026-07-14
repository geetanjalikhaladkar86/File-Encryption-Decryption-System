package com.example.filesecurity.service;



//import jakarta.crypto.Cipher;
//import jakarta.crypto.spec.IvParameterSpec;
//import jakarta.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Service
public class FileEncryptionService {

  // 16-byte demo key. Replace with secure key management in production.
  private static final byte[] KEY = "0123456789abcdef".getBytes();

  public byte[] encrypt(byte[] plain) throws Exception {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      byte[] iv = new byte[16];
      new SecureRandom().nextBytes(iv);
      IvParameterSpec ivSpec = new IvParameterSpec(iv);
      SecretKeySpec keySpec = new SecretKeySpec(KEY, "AES");
      cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
      byte[] encrypted = cipher.doFinal(plain);

      byte[] out = new byte[iv.length + encrypted.length];
      System.arraycopy(iv, 0, out, 0, iv.length);
      System.arraycopy(encrypted, 0, out, iv.length, encrypted.length);
      return out;
  }

  public byte[] decrypt(byte[] withIv) throws Exception {
      if(withIv.length < 16) throw new IllegalArgumentException("Invalid data");
      byte[] iv = new byte[16];
      System.arraycopy(withIv, 0, iv, 0, 16);
      int encLen = withIv.length - 16;
      byte[] enc = new byte[encLen];
      System.arraycopy(withIv, 16, enc, 0, encLen);

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      SecretKeySpec keySpec = new SecretKeySpec(KEY, "AES");
      cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
      return cipher.doFinal(enc);
  }
}
