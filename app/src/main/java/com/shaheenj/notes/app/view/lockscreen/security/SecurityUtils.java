package com.shaheenj.notes.app.view.lockscreen.security;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
 
public class SecurityUtils implements ISecurityUtils {

    private static final SecurityUtils ourInstance = new SecurityUtils();

    public static SecurityUtils getInstance() {
        return ourInstance;
    }

    private SecurityUtils() {

    }

    private KeyStore loadKeyStore() throws SecurityException {
        try {
            final KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            return keyStore;
        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | CertificateException
                | IOException e) {
            e.printStackTrace();
            throw new SecurityException(
                    "Can not load keystore:" + e.getMessage(),
                    SecurityUtilsErrorCodes.ERROR_LOAD_KEY_STORE
            );
        }
    }

    @Override
    public String encode(@NonNull Context context, String alias, String input, boolean isAuthorizationRequared)
            throws SecurityException {
        try {
            final Cipher cipher = getEncodeCipher(context, alias, isAuthorizationRequared);
            final byte[] bytes = cipher.doFinal(input.getBytes());
            return Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            throw new SecurityException(
                    "Error while encoding : " + e.getMessage(),
                    SecurityUtilsErrorCodes.ERROR_ENCODING
            );
        }
    }

 
    private Cipher getEncodeCipher(@NonNull Context context, String alias, boolean isAuthenticationRequired)
            throws SecurityException {
        final Cipher cipher = getCipherInstance();
        final KeyStore keyStore = loadKeyStore();
        generateKeyIfNecessary(context, keyStore, alias, isAuthenticationRequired);
        initEncodeCipher(cipher, alias, keyStore);
        return cipher;

    }

    private boolean generateKeyIfNecessary(@NonNull Context context, @NonNull KeyStore keyStore, String alias,
                           boolean isAuthenticationRequired) {
        try {
            return keyStore.containsAlias(alias) || generateKey(context, alias, isAuthenticationRequired);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean generateKey(Context context, String keystoreAlias, boolean isAuthenticationRequired) {
        return generateKey(keystoreAlias, isAuthenticationRequired);
    }

    private String decode(String encodedString, Cipher cipher) throws SecurityException {
        try {
            final byte[] bytes = Base64.decode(encodedString, Base64.NO_WRAP);
            return new String(cipher.doFinal(bytes));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            throw  new SecurityException(
                    "Error while decoding: " + e.getMessage(),
                    SecurityUtilsErrorCodes.ERROR_DEENCODING
            );
        }
    }

    @Override
    public String decode(String alias, String encodedString) throws SecurityException {
        try {
            final Cipher cipher = getCipherInstance();
            initDecodeCipher(cipher, alias);
            final byte[] bytes = Base64.decode(encodedString, Base64.NO_WRAP);
            return new String(cipher.doFinal(bytes));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            throw  new SecurityException(
                    "Error while decoding: " + e.getMessage(),
                    SecurityUtilsErrorCodes.ERROR_DEENCODING
            );
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean generateKey(String keystoreAlias, boolean isAuthenticationRequired)  {
        try {
            final KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
            keyGenerator.initialize(
                    new KeyGenParameterSpec.Builder(keystoreAlias,
                            KeyProperties.PURPOSE_ENCRYPT |
                                    KeyProperties.PURPOSE_DECRYPT)
                            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                            .setUserAuthenticationRequired(isAuthenticationRequired)
                            .build());
            keyGenerator.generateKeyPair();
            return true;

        } catch ( NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException exc) {
            exc.printStackTrace();
            return false;
        }
    }

    private Cipher getCipherInstance() throws SecurityException {
        try {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            throw new SecurityException(
                    "Can not get instance of Cipher object" + e.getMessage(),
                    SecurityUtilsErrorCodes.ERROR_GET_CIPHER_INSTANCE
            );
        }
    }


    private void initDecodeCipher(Cipher cipher, String alias) throws SecurityException {
        try {
            final KeyStore keyStore = loadKeyStore();
            final PrivateKey key  = (PrivateKey) keyStore.getKey(alias, null);
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException
                | InvalidKeyException e) {
            e.printStackTrace();
            throw  new SecurityException(
                    "Error init decode Cipher: " + e.getMessage(),
                    SecurityUtilsErrorCodes.ERROR_INIT_DECODE_CIPHER
            );
        }

    }

    private void initEncodeCipher(Cipher cipher, String alias, KeyStore keyStore)
            throws SecurityException {
        try {
            final PublicKey key = keyStore.getCertificate(alias).getPublicKey();
            final PublicKey unrestricted = KeyFactory.getInstance(key.getAlgorithm()).generatePublic(
                    new X509EncodedKeySpec(key.getEncoded()));
            final OAEPParameterSpec spec = new OAEPParameterSpec("SHA-256", "MGF1",
                    MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.ENCRYPT_MODE, unrestricted, spec);
        } catch (KeyStoreException | InvalidKeySpecException |
                NoSuchAlgorithmException | InvalidKeyException |
                InvalidAlgorithmParameterException e) {
            throw new SecurityException(
                    "Can not initialize Encode Cipher:" + e.getMessage(),
                    SecurityUtilsErrorCodes.ERROR_INIT_ENDECODE_CIPHER
            );
        }
    }

    @Override
    public boolean isKeystoreContainAlias(String alias) throws SecurityException {
        final KeyStore keyStore = loadKeyStore();
        try {
            return keyStore.containsAlias(alias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            throw new SecurityException(
                    e.getMessage(),
                    SecurityUtilsErrorCodes.ERROR_KEY_STORE
            );
        }
    }


    /**
     * Delete key from KeyStore.
     * @param alias KeyStore alias.
     * @throws SecurityException throw Exception if something went wrong.
     */
    @Override
    public void deleteKey(String alias) throws SecurityException {
        final KeyStore keyStore = loadKeyStore();
        try {
            keyStore.deleteEntry(alias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            throw new SecurityException(
                    "Can not delete key: " + e.getMessage(),
                    SecurityUtilsErrorCodes.ERROR_DELETE_KEY
            );
        }
    }

}
