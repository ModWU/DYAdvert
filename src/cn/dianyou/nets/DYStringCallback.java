package cn.dianyou.nets;

import okhttp3.Response;

public abstract class DYStringCallback extends DYCallback<String> {

	@Override
	public String parseNetworkResponse(Response response, int id) throws Exception {
		return response.body().string();
	}

}
