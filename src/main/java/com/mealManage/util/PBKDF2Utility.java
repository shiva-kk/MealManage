package com.mealManage.util;

import static org.springframework.security.crypto.util.EncodingUtils.concatenate;
import static org.springframework.security.crypto.util.EncodingUtils.subArray;

import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.Date;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PBKDF2Utility  implements PasswordEncoder {
	private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
	private static final int DEFAULT_HASH_WIDTH = 160;
	private static final int DEFAULT_ITERATIONS = 1000;

	private final BytesKeyGenerator saltGenerator = KeyGenerators.secureRandom();

	private final byte[] secret;
	private final int hashWidth;
	private final int iterations;

	public PBKDF2Utility() {
		this("");
	}
/**
 * This method used to encrypt the value what we are passing.
 * @param secret
 */
	public PBKDF2Utility(CharSequence secret) {
		this(secret, DEFAULT_ITERATIONS, DEFAULT_HASH_WIDTH);
	}

	
	public PBKDF2Utility(CharSequence secret, int iterations, int hashWidth) {
		this.secret = Utf8.encode(secret);
		this.iterations = iterations;
		this.hashWidth = hashWidth;
	}

	public String encode(CharSequence rawPassword) {
		byte[] salt = this.saltGenerator.generateKey();
		byte[] encoded = encode(rawPassword, salt);
		return String.valueOf(Hex.encode(encoded));
	}

	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		byte[] digested = Hex.decode(encodedPassword);
		byte[] salt = subArray(digested, 0, this.saltGenerator.getKeyLength());
		return matches(digested, encode(rawPassword, salt));
	}

	/**
	 * Constant time comparison to prevent against timing attacks.
	 */
	private static boolean matches(byte[] expected, byte[] actual) {
		if (expected.length != actual.length) {
			return false;
		}

		int result = 0;
		for (int i = 0; i < expected.length; i++) {
			result |= expected[i] ^ actual[i];
		}
		return result == 0;
	}

	private byte[] encode(CharSequence rawPassword, byte[] salt) {
		try {
			PBEKeySpec spec = new PBEKeySpec(rawPassword.toString().toCharArray(),
					concatenate(salt, this.secret), this.iterations, this.hashWidth);
			SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
			return concatenate(salt, skf.generateSecret(spec).getEncoded());
		}
		catch (GeneralSecurityException e) {
			throw new IllegalStateException("Could not create hash", e);
		}
	}

	/** This method used for check the token expired or not **/
	public boolean updateForgotPasswordToken(Date newAuthTokenGeneratedTime) {
		boolean status = false;
		try {
			status = checkTokenExpiry(newAuthTokenGeneratedTime, 500);
			return status;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean checkTokenExpiry(Date generatedTimestamp, int maxMinutes) throws ParseException {
		Date date = generatedTimestamp;
		Date currentDate = new Date();
		float ms = (currentDate.getTime() - date.getTime()) / (60 * 1000);
		System.out.println(ms);
		if (ms <= maxMinutes) {
			return true;
		} else {
			return false;
		}
	}
}
