package qora.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import utils.Pair;
import org.whispersystems.curve25519.java.*;

public class Ed25519 {

	public static void sha512(byte[] in, long length, byte[] out) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
			messageDigest.update(in, 0, (int)length);
			byte[] digest = messageDigest.digest();
			System.arraycopy(digest, 0, out, 0, digest.length);
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError(e);
		}
	}
	
	public static void ed25519_create_keypair(byte[] public_key, byte[] private_key, byte[] seed)
	{
	    ge_p3 A = new ge_p3();

	    sha512(seed, 32, private_key);
	    private_key[0] &= 248;
	    private_key[31] &= 63;
	    private_key[31] |= 64;

	    ge_scalarmult_base.ge_scalarmult_base(A, private_key);
	    ge_p3_tobytes.ge_p3_tobytes(public_key, A);	
	}
	
	public static Pair<byte[], byte[]> createKeyPair(byte[] seed)
	{
		byte[] privateKey = new byte[64];
		byte[] publicKey = new byte[32];
		
		ed25519_create_keypair(publicKey, privateKey, seed);
		
		return new Pair<byte[], byte[]>(privateKey, publicKey);
	}
}
	