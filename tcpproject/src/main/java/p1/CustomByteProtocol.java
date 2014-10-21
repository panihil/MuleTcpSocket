package p1;

import org.mule.transport.tcp.protocols.AbstractByteProtocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CustomByteProtocol extends AbstractByteProtocol {

	// TODO - promote begin and end tokens to properties, provide getters
	// setters.
	// TODO - change token type to string
	// TODO - support regex for token compare.

	private String startToken;
	private String endToken;
private boolean removeEndDelimiter = false;
	public CustomByteProtocol() {
		super(false); // This protocol does not support streaming.
	}

	/**
	 * Read bytes until we see end token.
	 */
	public Object read(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte read[] = new byte[1];

		while (true) {

			if (safeRead(is, read) < 0) {

				return null;
			}
			byte b = read[0];

			// Just for POC tokens are hard coded bytes.
			// TODO - move check for start/end tokens to method that uses
			// regular expressions.
			/*
			 * if (b == '\u000b') { } else if (b == '\u001c') return
			 * baos.toByteArray();
			 * 
			 * else baos.write(b);
			 */
			 baos.write(b);
			if (hasEndToken(baos)) {
				
				byte[] retArray = baos.toByteArray();
				
				if( this.removeEndDelimiter )
				{
					byte[] retArray2 = new byte[retArray.length -  this.endToken.getBytes( ).length];
					System.arraycopy(retArray, 0, retArray2, 0, retArray.length -  this.endToken.getBytes( ).length);
					return retArray2;
				}
				else
				return baos.toByteArray();
			}

		}
	}

	private boolean hasEndToken(ByteArrayOutputStream bs) {

		byte[] end = this.endToken.getBytes( );
		
		if (CustomByteProtocol.indexOf(bs.toByteArray(),
				this.endToken.getBytes()) > -1)
			return true;
		else
			return false;
	}

	public static int indexOf(byte[] data, byte[] pattern) {
		int[] failure = computeFailure(pattern);

		int j = 0;
		if (data.length == 0)
			return -1;

		for (int i = 0; i < data.length; i++) {
			while (j > 0 && pattern[j] != data[i]) {
				j = failure[j - 1];
			}
			if (pattern[j] == data[i]) {
				j++;
			}
			if (j == pattern.length) {
				return i - pattern.length + 1;
			}
		}
		return -1;
	}

	/**
	 * Computes the failure function using a boot-strapping process, where the
	 * pattern is matched against itself.
	 */
	private static int[] computeFailure(byte[] pattern) {
		int[] failure = new int[pattern.length];

		int j = 0;
		for (int i = 1; i < pattern.length; i++) {
			while (j > 0 && pattern[j] != pattern[i]) {
				j = failure[j - 1];
			}
			if (pattern[j] == pattern[i]) {
				j++;
			}
			failure[i] = j;
		}

		return failure;
	}

	public String getStartToken() {
		return startToken;
	}

	public void setStartToken(String startToken) {
		this.startToken = startToken;
	}

	public String getEndToken() {
		return endToken;
	}

	public void setEndToken(String endToken) {
		this.endToken = endToken;
	}

	public boolean isRemoveEndDelimiter() {
		return removeEndDelimiter;
	}

	public void setRemoveEndDelimiter(boolean removeEndDelimiter) {
		this.removeEndDelimiter = removeEndDelimiter;
	}

}
