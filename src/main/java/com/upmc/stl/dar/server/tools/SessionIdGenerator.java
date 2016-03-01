package com.upmc.stl.dar.server.tools;

import java.security.SecureRandom;

public class SessionIdGenerator
{
	private static final Integer length = 100;
	
	public static synchronized String generateUniqueToken() { 
	    byte random[] = new byte[SessionIdGenerator.length];
	    SecureRandom randomGenerator = new SecureRandom();
	    StringBuffer buffer = new StringBuffer();

	    randomGenerator.nextBytes(random);

	    for (int j = 0; j < random.length; j++) {
	        byte b1 = (byte) ((random[j] & 0xf0) >> 4);
	        byte b2 = (byte) (random[j] & 0x0f);
	        if (b1 < 10)
	            buffer.append((char) ('0' + b1));
	        else
	            buffer.append((char) ('A' + (b1 - 10)));
	        if (b2 < 10)
	            buffer.append((char) ('0' + b2));
	        else
	            buffer.append((char) ('A' + (b2 - 10)));
	    }
	    
	    return (buffer.toString());
	}
}
