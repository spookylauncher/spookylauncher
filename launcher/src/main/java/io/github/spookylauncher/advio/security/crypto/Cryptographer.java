package io.github.spookylauncher.advio.security.crypto;

import io.github.spookylauncher.advio.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.security.Key;
import java.util.Objects;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Cryptographer {
    public final String algorithm;

	public Cryptographer(CryptographyAlgorithm algorithm) {
		this(algorithm.getAlgorithmName());
	}

	public Cryptographer(String algorithm) {
		this.algorithm = algorithm;
	}
    
    public void encryptFile(File file, byte[] key) {
    	try {
        	byte[] input = encrypt(Files.newInputStream(file.toPath()), key);
        	OutputStream output = Files.newOutputStream(file.toPath());
        	output.write(input);
        	output.close();
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void decryptFile(File file, byte[] key) {
    	try {
        	byte[] input = decrypt(Files.newInputStream(file.toPath()), key);
        	OutputStream output = Files.newOutputStream(file.toPath());
        	output.write(input);
        	output.close();
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public InputStream encryptToStream(byte[] data, byte[] key) {
    	return new ByteArrayInputStream(encrypt(data, key));
    }
    
    public InputStream decryptToStream(byte[] encrypted, byte[] key) {
    	return new ByteArrayInputStream(Objects.requireNonNull(decrypt(encrypted, key)));
    }
    
    public InputStream encryptStream(InputStream stream, byte[] key) {
    	return new ByteArrayInputStream(encrypt(IOUtils.readBytes(stream), key));
    }
    
    public InputStream decryptStream(InputStream stream, byte[] key) {
    	return new ByteArrayInputStream(Objects.requireNonNull(decrypt(IOUtils.readBytes(stream), key)));
    }
    
    public byte[] encrypt(InputStream stream, byte[] key) {
    	return encrypt(IOUtils.readBytes(stream), key);
    }
    
    public byte[] decrypt(InputStream stream, byte[] key) {
    	return decrypt(IOUtils.readBytes(stream), key);
    }
    
    public byte[] encrypt(byte[] data, byte[] key) {
    	try {
            Key keyInstance = generateKey(key);
            Cipher c = Cipher.getInstance(algorithm);
            c.init(Cipher.ENCRYPT_MODE, keyInstance);
			return c.doFinal(data);
    	} catch(Exception e) {
    		e.printStackTrace();
    		return new byte[0];
    	}
    }

    public byte[] decrypt(byte[] encrypted, byte[] key) {
    	try {
            Key keyInstance = generateKey(key);
            Cipher c = Cipher.getInstance(algorithm);
            c.init(Cipher.DECRYPT_MODE, keyInstance);
			return c.doFinal(encrypted);
    	} catch(Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    private Key generateKey(byte[] key) throws Exception {
		if(key.length != 16 && "AES;DESede".contains(algorithm)) {
			if(key.length < 16) {
				byte[] newKey = new byte[16];

				for(int i = 0;i < key.length;i++) {
					newKey[i] = key[i];
				}

				key = newKey;
			} else throw new IllegalArgumentException("maximum key length is 16 bytes");
		}

		return new SecretKeySpec(key, algorithm);
    }
}