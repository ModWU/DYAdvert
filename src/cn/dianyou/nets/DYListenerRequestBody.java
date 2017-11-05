package cn.dianyou.nets;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class DYListenerRequestBody extends RequestBody {
	
	protected RequestBody delegate;
	
	protected Listener listener;
	
	protected CountingSink countingSink;
	
	public DYListenerRequestBody(RequestBody delegate, Listener listener) {
		this.delegate = delegate;
		this.listener = listener;
	}

	@Override
	public MediaType contentType() {
		return delegate.contentType();
	}
	
	@Override
	public long contentLength() throws IOException {
		try {
			return delegate.contentLength();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return -1;
	}

	@Override
	public void writeTo(BufferedSink sink) throws IOException {
		countingSink = new CountingSink(sink);
		BufferedSink bufferedSink = Okio.buffer(countingSink);
		delegate.writeTo(bufferedSink);
		bufferedSink.flush();
	}
	
	private final class CountingSink extends ForwardingSink {
		
		private long bytesWritten = 0;

		public CountingSink(Sink delegate) {
			super(delegate);
		}
		
		@Override
		public void write(Buffer source, long byteCount) throws IOException {
			super.write(source, byteCount);
			bytesWritten += byteCount;
			listener.onRequestProgress(bytesWritten, contentLength());
		}
		
	}
	
	
	public interface Listener {
		public void onRequestProgress(long bytesWritten, long contentLength);
	}

}
