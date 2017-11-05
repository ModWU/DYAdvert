package cn.dianyou.nets;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import okhttp3.Response;

public abstract class DYBitmapCallback extends DYCallback<Bitmap> {

	@Override
	public Bitmap parseNetworkResponse(Response response, int id) throws Exception {
		return BitmapFactory.decodeStream(response.body().byteStream());
	}


}
