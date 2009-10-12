package org.databene.contiperf;

import java.io.Closeable;

public class Util {

	public static void close(Closeable resource) {
	    if (resource != null) {
	    	try {
	    		resource.close();
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
    }

}
