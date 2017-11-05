package cn.dianyou.nets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Handler;
import android.os.Looper;
import okhttp3.Response;

public abstract class DYFileCallBack extends DYCallback<File> {
	private String destFileDir;
	
	private String destFileName;
	
	private Handler mHandler = null;
	
	public DYFileCallBack(String destFileDir, String destFileName) {
		this.destFileDir = destFileDir;
		this.destFileName = destFileName;
		if(mHandler == null)
			mHandler = new Handler(Looper.getMainLooper());
	}
	
	@Override
	public File parseNetworkResponse(Response response, int id) throws Exception {
		return saveFile(response, id);
	}
	

	private File saveFile(Response response, final int id) throws IOException {
		InputStream is = null;
		byte[] buf = new byte[2048];
		int len = 0;
		FileOutputStream fos = null;
		try {
			is = response.body().byteStream();
			final long total = response.body().contentLength();
			
			long sum = 0;
			
			File dir = new File(destFileDir);
			if(!dir.exists()) {
				dir.mkdirs();
			}
			
			File file = new File(dir, destFileName);
			fos = new FileOutputStream(file);
			while((len = is.read(buf)) != -1) {
				sum += len;
				fos.write(buf, 0, len);
				fos.flush();
				final long finalSum = sum;
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						inProgress(finalSum * 1.0f / total,total, id);
					}
				});
			}
			
			fos.flush();
			
			return file;
			
		} finally {
			try {
				response.body().close();
				if(is != null) is.close();
			} catch(IOException e) {	
			}
			
			try {
				if(fos != null) fos.close();
			} catch(IOException e) {}
			
		}
	}
}
