package org.processmonitor.generator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class DiagramGeneratorTest {

	protected static boolean isEqual(InputStream stream1, InputStream stream2)
			throws IOException {
			
			      ReadableByteChannel channel1 = Channels.newChannel(stream1);
			      ReadableByteChannel channel2 = Channels.newChannel(stream2);
			
			      ByteBuffer buffer1 = ByteBuffer.allocateDirect(1024);
			      ByteBuffer buffer2 = ByteBuffer.allocateDirect(1024);
			
			      try {
			          while (true) {
			
			              int bytesReadFromStream1 = channel1.read(buffer1);
			              int bytesReadFromStream2 = channel2.read(buffer2);
			
			              if (bytesReadFromStream1 == -1 || bytesReadFromStream2 == -1) 
			            	  return bytesReadFromStream1 == bytesReadFromStream2;
			
			              buffer1.flip();
			              buffer2.flip();
			
			              for (int i = 0; i < Math.min(bytesReadFromStream1, bytesReadFromStream2); i++)
			                  if (buffer1.get() != buffer2.get())
			                      return false;
			
			              buffer1.compact();
			              buffer2.compact();
			          }
			      } catch (IOException e) {
			    	  System.err.println( e.getStackTrace());
			    	  throw e;
			      } finally {
			          if (stream1 != null) stream1.close();
			          if (stream2 != null) stream2.close();
			      }
			  }

	public DiagramGeneratorTest() {
		super();
	}

}