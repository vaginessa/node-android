package com.iwebpp.node;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.List;

import com.iwebpp.libuvpp.cb.TimerCallback;
import com.iwebpp.libuvpp.handles.TimerHandle;

import android.util.Log;

public final class Util {
	private static final String TAG = "Util";
	
	// Buffer
    public static boolean isBuffer(Object chunk) {
    	return chunk instanceof ByteBuffer;
    } 
    
    public static boolean isString(Object chunk) {
    	return chunk instanceof String;
    } 
    
    public static boolean isNullOrUndefined(Object chunk) {
    	return chunk == null;
    }

    public static boolean isUndefined(Object chunk) {
    	return chunk == null;
    }
    
    public static boolean isNull(Object chunk) {
    	return chunk == null;
    }
    
    public static int chunkLength(Object chunk) {
    	if (isBuffer(chunk)) {
    		ByteBuffer bb = (ByteBuffer)chunk;
    		return bb.capacity();
    	}
    	
    	if (isString(chunk)) {
    		String s = (String)chunk;
    		return s.length();
    	}
    	
		return 0;
    } 
    
    public static int stringByteLength(String chunk, String encoding) throws UnsupportedEncodingException {
    	if (isString(chunk))
    		return chunk.getBytes(encoding).length;
    	
		return 0;
    } 
    
    public static Object chunkSlice(Object chunk, int start, int end) {
    	if (isBuffer(chunk)) {
    		ByteBuffer bb = (ByteBuffer)chunk;
    		int opos = bb.position(); bb.position(start);
    		int olmt = bb.limit(); bb.limit(end);
    		
			ByteBuffer rb = bb.slice();
			bb.limit(olmt); bb.position(opos);
			
			return rb;
    	}
    	
    	if (isString(chunk)) {
    		String s = (String)chunk;
    		
    		return s.substring(start, end);
    	}
    	
    	return null;
    }
    
    public static Object chunkSlice(Object chunk, int start) {
    	if (isBuffer(chunk)) {
    		ByteBuffer bb = (ByteBuffer)chunk;
    		int opos = bb.position(); bb.position(start);
    		
    		ByteBuffer rb = bb.slice();
			bb.position(opos);
			
			return rb;
    	}
    	
    	if (isString(chunk)) {
    		String s = (String)chunk;
    		
    		return s.substring(start, s.length());
    	}
    	
    	return null;
    }

    public static boolean zeroString(String s) {
    	if (s == null) 
    		return true;
    	else 
    		return s == "";
    }

    public static ByteBuffer concatByteBuffer(List<Object> list, int length) {
    	if (length <= 0) {
    		length = 0;
    		
    		for (Object b : list) length += ((ByteBuffer) b).capacity();
    	}
    		
    	if (length > 0) {
    		ByteBuffer bb = ByteBuffer.allocate(length);

    		for (Object b : list) {
    			bb.put((ByteBuffer)b);
    			
    			((ByteBuffer)b).flip();
    		}
    		bb.flip();
    		
    		return bb;
    	} else 
    		return null;
    }
    
    public static String chunkToString(Object chunk, String encoding) {
    	if (isString(chunk)) {			
    		return (String) chunk;			
    	} else if (isBuffer(chunk)) {
    		// decode chunk to string
    		try {
				return Charset.forName(encoding).newDecoder().decode((ByteBuffer)chunk).toString();
			} catch (CharacterCodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
		return "invalid chunk";
    }
    
    public static interface nexTickCallback {
    	void onNextTick() throws Exception;
    }
    
    // fire on next tick
    public static void nextTick(NodeContext ctx, final nexTickCallback next) {
        final TimerHandle timer = new TimerHandle(ctx.getLoop());

        timer.setCloseCallback(new TimerCallback() {
            @Override
            public void onTimer(final int i) throws Exception {
                Log.d(TAG, "nextTick timer closed");
            }
        });

        timer.setTimerFiredCallback(new TimerCallback() {
            @Override
            public void onTimer(final int status) throws Exception {
                Log.d(TAG, "nextTick timer fired");

                next.onNextTick();
                
                timer.close();
            }
        });

        timer.start(0, 0);
    }
        
}