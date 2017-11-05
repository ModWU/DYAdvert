package cn.dianyou.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;
import cn.dianyou.advert.WebViewActivity;
import cn.dianyou.beans.ImageInfo;
import cn.dianyou.beans.PartImageInfo;
import cn.dianyou.nets.DYBitmapCallback;
import cn.dianyou.nets.DYHttpUtils;
import cn.dianyou.nets.DYStringCallback;
import cn.dianyou.utils.ContantsUtils;
import cn.dianyou.utils.DEncodingUtils;
import cn.dianyou.utils.FileManageUtils;
import cn.dianyou.utils.SharedPreferencesUtils;
import cn.dianyou.views.ReboundViewPager;
import cn.dianyou.views.ReboundViewPager.OnPagerItemListener;
import cn.dianyou.views.ReboundViewPager.TransitionEffect;
import cn.dianyou.views.RoundCornerImageView;
import okhttp3.Call;

public class DYViewPagerManager extends DYAdvertManager<DYViewPagerManager> {
	
	private static volatile DYViewPagerManager dyViewPagerManager;
	
	private static HashMap<ViewGroup, List<Bitmap>> bitmaps = null;
	
	private ReboundViewPager reboundViewPager;
	
	private String advertId = null;
	private String type = null;
	

	DYViewPagerManager(Context context) {
		super(context);
		if(bitmaps == null) {
			bitmaps = new HashMap<ViewGroup, List<Bitmap>>(); 
		}
	}
	

	static DYViewPagerManager getInstance(Context context) {
		if(dyViewPagerManager == null) {
			synchronized (DYBannerManager.class) {
				if(dyViewPagerManager == null)
					dyViewPagerManager = new DYViewPagerManager(context);
				
			}
		}
		return dyViewPagerManager;
	}


	@Override
	public DYViewPagerManager loadEarlyAdvertData() {
		return this;
	}
	
	public void show(ViewGroup parent) {
		show(parent, null);
	}
	
	public void show(ViewGroup parent, List<Bitmap> failListBmp) {
		if(parent == null) {
			return;
		}
		Log.i("chao", ".......null.....show");
		//---获取广告id---//
		advertId = SharedPreferencesUtils.newInstance(context, "filename_dianyou_authorize").getString("dianyou_login_current_id", null);
		type = SharedPreferencesUtils.newInstance(context, "filename_dianyou_authorize").getString("dianyou_login_current_id_type", "1");
		if(advertId == null) {
			advertId = SharedPreferencesUtils.newInstance(context, "filename_dianyou_authorize").getString("dianyou_unique_id", null);//dianyou_login_current_id dianyou_login_current_id_type
			type = SharedPreferencesUtils.newInstance(context, "filename_dianyou_authorize").getString("dianyou_unique_id_type", "1");
		}
		if(advertId == null) {
			Log.i("INFO", "obtain AdvertId fail!");
			return;
		}
		//-------------//
		final ViewGroup tempParent = parent;
		final List<Bitmap> tempFailListBmp = failListBmp;
		
		findImage(new IFindImageListener() {

			@Override
			public void finished(List<ImageInfo> imgInfoList, List<Bitmap> failBmps, Map<String, Object> otherData, boolean isSuccess) {
				
				if(bitmaps == null) {
					return;
				}
				
				Activity activity = getActivity();
				
				if(activity != null && !activity.isFinishing()) {
					if(bitmaps.containsKey(tempParent)) {
						List<Bitmap> list = bitmaps.get(tempParent);
						for(Bitmap bmp : list) {
							if(bmp != null)
								bmp.recycle();
						}
						list.clear();
						tempParent.removeAllViews();
					}
					
					Log.i("chao", ".......null.....1");
					
					final boolean isCensus = (Boolean) otherData.get("isCensus");
					
					if(isSuccess) {
						List<Bitmap> allBmp = getAllBitmap(imgInfoList);
						if(allBmp != null && !allBmp.isEmpty()) {
							List<View> listAdView = getAllAdViews(allBmp);
							bitmaps.put(tempParent, allBmp);
							success_showAtParent(tempParent, imgInfoList, listAdView, isCensus);
						}
					} else {
						if(imgInfoList == null) {
							if(failBmps != null && !failBmps.isEmpty()) {
								List<View> listView = getAllAdViews(failBmps);
								bitmaps.put(tempParent, failBmps);
								fail_showAtParent(tempParent, listView);
							}
							Log.i("chao", ".......null.....");
						} else {
							List<Bitmap> allBmp = getAllBitmap(imgInfoList);
							if(allBmp == null || allBmp.isEmpty()) {
								if(failBmps != null && !failBmps.isEmpty()) {
									List<View> listView = getAllAdViews(failBmps);
									bitmaps.put(tempParent, failBmps);
									fail_showAtParent(tempParent, listView);
								}
							} else {
								List<View> listAdView = getAllAdViews(allBmp);
								if(listAdView != null && !listAdView.isEmpty()) {
									bitmaps.put(tempParent, allBmp);
									success_showAtParent(tempParent, imgInfoList, listAdView, isCensus);
								}
							}
							
						}
					}
					
					
				}
			}

			@Override
			public List<Bitmap> getFailBitmap() {
				return tempFailListBmp;
			}

			@Override
			public Activity getActivity() {
				Activity activity = null;
				if(tempParent != null && tempParent.getContext() instanceof Activity) {
					activity = (Activity) tempParent.getContext();
				} 
				return activity;
			}
		});
	}
	


	public void handleOnStop() {
		if(reboundViewPager != null)
			reboundViewPager.stopAutoGo();
	}
	
	public void handleOnStart() {
		if(reboundViewPager != null)
			reboundViewPager.autoGo(3000, 4000, true);
	}
	
	
	private ReboundViewPager createViewPager(Context context, List<ImageInfo> imgInfoList, List<View> listAdView, boolean isCensus, boolean isClickable) {
		
		final List<ImageInfo> tempImgInfoList = imgInfoList;
		final boolean tempIsCensus = isCensus;
		final Activity activity = (Activity) context;
		
		reboundViewPager = new ReboundViewPager(context);
		
		ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		
		reboundViewPager.setLayoutParams(vlp);
		
		reboundViewPager.setTransitionEffect(TransitionEffect.None);
		
		reboundViewPager.setFadeEnabled(false);
		
		reboundViewPager.setIndexDot(true);
		
		reboundViewPager.setDotCheckedColor(Color.parseColor("#79c73f"));
		
		reboundViewPager.setDotUnCheckedColor(Color.parseColor("#cec0bd"));
		
		//reboundViewPager.setDotLeftRadius(0.4f);
		
		reboundViewPager.setDotMarginRadius(0.15f);
		
		reboundViewPager.setDotBottomDistance(6);
		
		reboundViewPager.setDotSizeRadius(0.0035f);
		
		reboundViewPager.setBootAutoGo(true);
		
		if(isClickable && tempImgInfoList != null) {
			reboundViewPager.setOnPagerItemListener(new OnPagerItemListener() {
				
				@Override
				public void onPagerClick(View view, int position) {
					ImageInfo imageInfo = tempImgInfoList.get(position);
					if(imageInfo.isClickable()) {
						String clickUrl = imageInfo.getClickUrl();
						String webTitle = imageInfo.getTitle();
						
						if(tempIsCensus) {
							//统计数据  URL_ADVERT_CENSUS
							censusInfo(imageInfo.getId());
						}
						
						//跳转页面
						jumpToDetailsByClick(activity, webTitle, clickUrl);
					}
				}
			});
		}
			
		reboundViewPager.setPagerViews(listAdView);
	
		return reboundViewPager;
	}
	
	private void success_showAtParent(ViewGroup parent, List<ImageInfo> imgInfoList, List<View> listAdView, boolean isCensus) {
		if(parent == null || listAdView == null || listAdView.isEmpty()) {
			return;
		}
		
		ReboundViewPager rvp = createViewPager(parent.getContext(),imgInfoList, listAdView, isCensus, true);
		
		rvp.autoGo(3000, 4000, true);
		parent.addView(rvp);
		parent.setVisibility(View.VISIBLE);
	}
	
	//跳转到指定页
	private void jumpToDetailsByClick(Activity activity, String webTitle, String clickUrl) {
		Log.i("INFO", "跳转页面");
		Intent intent = new Intent(activity, WebViewActivity.class);
		intent.putExtra(WebViewActivity.WEBVIEW_URL, clickUrl);
		intent.putExtra(WebViewActivity.WEBVIEW_TITLE, webTitle);
		activity.startActivity(intent);
	}
	
	//统计点击次数 ，上传至服务器
	private void censusInfo(String imageId) {
		Log.i("INFO", "统计信息");
		//---获取广告id---//
		String advertId = SharedPreferencesUtils.newInstance(context, "filename_dianyou_authorize").getString("dianyou_login_current_id", null);
		String userType = SharedPreferencesUtils.newInstance(context, "filename_dianyou_authorize").getString("dianyou_login_current_id_type", "1");
		if(advertId == null) {
			advertId = SharedPreferencesUtils.newInstance(context, "filename_dianyou_authorize").getString("dianyou_unique_id", null);//dianyou_login_current_id dianyou_login_current_id_type
			userType = SharedPreferencesUtils.newInstance(context, "filename_dianyou_authorize").getString("dianyou_unique_id_type", "1");
		}
		
		if(advertId == null)
			return;
		
		advertId = DEncodingUtils.encoding(advertId, "UTF-8");
		userType = DEncodingUtils.encoding(userType, "UTF-8");
		String imgId = DEncodingUtils.encoding(imageId, "UTF-8");
		
		Map<String, String> censusParams = new HashMap<String, String>();
		censusParams.put("Id", advertId);
		censusParams.put("Type", userType);
		censusParams.put("ImageId", imgId);
		
		DYHttpUtils.getInstance().post().url(ContantsUtils.DianYou_url.URL_ADVERT_CENSUS).paramJSONType().params(censusParams).build().execute(new DYStringCallback() {

			@Override
			public void onError(Call call, Exception e, int id) {
				Log.i("INFO", "censusInfo request ex:" + e.toString());
			}

			@Override
			public void onResponse(String data, int id) {
				try {
					data = DEncodingUtils.decoding(data, "UTF-8");
					Log.i("INFO", "census data-->" + data);
					JSONObject jsonObj = new JSONObject(data);
					int code = jsonObj.getInt("Code");
					if(code == 0) {
						Log.i("INFO", "censusInfo success!");
					}
				} catch(Exception e) {
					Log.i("INFO", "censusInfo ex:" + e.toString());
				}
			}
			
		});
	}
	
	
	
	private void fail_showAtParent(ViewGroup parent, List<View> listAdView) {
		if(parent == null || listAdView == null || listAdView.isEmpty()) {
			return;
		}
		
		ReboundViewPager rvp = createViewPager(parent.getContext(), null, listAdView, false, false);
		rvp.autoGo(2000, 3000, true);
		parent.addView(rvp);
		parent.setVisibility(View.VISIBLE);
	}


	private List<View> getAllAdViews(List<Bitmap> bmps) {
		
		if(bmps == null || bmps.size() <= 0) {
			return null;
		}
		
		List<View> listViews = new ArrayList<View>();
		for(Bitmap bmp : bmps) {
			if(bmp != null) {
				RoundCornerImageView imageView = new RoundCornerImageView(context);
				imageView.setRoundDegree(0);
				imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
				imageView.setImageBitmap(bmp);
				imageView.setScaleType(ScaleType.FIT_XY);
				listViews.add(imageView);
			}
		}
		
		return listViews;
	}


	@Override
	public void findImage(DYAdvertManager.IFindImageListener listener) {
		final IFindImageListener tempListener = listener;
		
		String advertId = DEncodingUtils.encoding(this.advertId, "UTF-8");
		String userType = DEncodingUtils.encoding(this.type, "UTF-8");
		String advertType = DEncodingUtils.encoding("1", "UTF-8");
		
		Map<String, String> reqMapParams = new HashMap<String, String>();
		reqMapParams.put("Id", advertId);
		reqMapParams.put("Type", userType);
		reqMapParams.put("AdType", advertType);
		
		Log.i("INFO", "reqMapParams: " + reqMapParams.toString());
		
		Log.i("chao", ".......null.....findimage");
		
		DYHttpUtils.getInstance().post().url(ContantsUtils.DianYou_url.URL_ADVERT_INFO).paramJSONType().params(reqMapParams).build().execute(new DYStringCallback() {

			@Override
			public void onError(Call call, Exception e, int id) {
				Log.i("INFO", "findImage ex: " + e.toString());
				showWhenFail(null, false, tempListener);
				
				Log.i("chao", ".......null.....findimage onError");
			}

			@Override
			public void onResponse(String data, int id) {
				try {
					data = DEncodingUtils.decoding(data, "UTF-8");
					Log.i("INFO", "findImage data: " + data);
					JSONObject jsonObj = new JSONObject(data);
					int code = jsonObj.optInt("Code", -1);
					if(code == 0) {
						boolean isCensus = jsonObj.optBoolean("IsCensus", false);
						JSONArray jsonArr = jsonObj.getJSONArray("ImgInfo");
						int count = jsonArr.length();
						Log.i("sort", ".......count:..." + count);
						if(count > 0) {
							List<ImageInfo> imgList = new LinkedList<ImageInfo>();
							for(int i = 0; i < count; i++) {
								ImageInfo imageInfo = new ImageInfo();
								JSONObject imgObj = jsonArr.getJSONObject(i);
								String title = imgObj.getString("Title");
								String url = imgObj.getString("Url");
								String imgId = imgObj.getString("ImageId");
								int sort = imgObj.getInt("Sort");
								boolean isClickable = imgObj.getBoolean("IsClickable");
								String clickUrl = imgObj.getString("ClickUrl");
								
								List<PartImageInfo> partImgInfoList = null;
								JSONArray subJsonArr = imgObj.getJSONArray("PartUrlInfo");
								int partCount = subJsonArr.length();
								if(partCount > 0) {
									partImgInfoList = new ArrayList<PartImageInfo>();
									for(int j = 0; j < partCount; j++) {
										PartImageInfo partImgInfo = new PartImageInfo();
										JSONObject partImgObj = subJsonArr.getJSONObject(j);
										String partTitle = partImgObj.getString("Title");
										String partUrl = partImgObj.getString("PartUrl");
										float animDregee = Float.valueOf(partImgObj.get("AnimDregee").toString());
										float rotateRadiusX = Float.valueOf(partImgObj.get("RotateRadiusX").toString());
										float rotateRadiusY = Float.valueOf(partImgObj.get("RotateRadiusY").toString());
										boolean isCenter = partImgObj.getBoolean("IsCenter");
										int width = partImgObj.getInt("Width");
										int height = partImgObj.getInt("Height");
										boolean isAnimation = partImgObj.getBoolean("IsAnimation");
										float leftRadius = Float.valueOf(partImgObj.get("LeftRadius").toString());
										float topRadius = Float.valueOf(partImgObj.get("TopRadius").toString());
										float leftOffset = Float.valueOf(partImgObj.get("LeftOffset").toString());
										float topOffset = Float.valueOf(partImgObj.get("TopOffset").toString());
										float rotateXOffset = Float.valueOf(partImgObj.get("RotateXOffset").toString());
										float rotateYOffset = Float.valueOf(partImgObj.get("RotateYOffset").toString());
										partImgInfo.setTitle(partTitle);
										partImgInfo.setUrl(partUrl);
										partImgInfo.setAnimDregee(animDregee);
										partImgInfo.setRotateRadiusX(rotateRadiusX);
										partImgInfo.setRotateRadiusY(rotateRadiusY);
										partImgInfo.setCenter(isCenter);
										partImgInfo.setWidth(width);
										partImgInfo.setHeight(height);
										partImgInfo.setAnimation(isAnimation);
										partImgInfo.setLeftRadius(leftRadius);
										partImgInfo.setTopRadius(topRadius);
										partImgInfo.setLeftOffset(leftOffset);
										partImgInfo.setTopOffset(topOffset);
										partImgInfo.setRotateXOffset(rotateXOffset);
										partImgInfo.setRotateRadiusY(rotateYOffset);
										partImgInfoList.add(partImgInfo);
									}
								}
								
								imageInfo.setTitle(title);
								imageInfo.setUrl(url);
								imageInfo.setId(imgId);
								imageInfo.setSort(sort);
								imageInfo.setClickable(isClickable);
								imageInfo.setClickUrl(clickUrl);
								imageInfo.setPartImageInfoList(partImgInfoList);
								imgList.add(imageInfo);
							}
							//排序
							Collections.sort(imgList);
							
							Log.i("INFO", "...result-->" + imgList.toString());
							
							requestImagesAndShow(isCensus, imgList, tempListener);
							
						} else {
							Log.i("INFO", "findImage is empty!");
							showWhenFail(null, false, tempListener);
						}
					} else {
						Log.i("INFO", "findImage code error : " + code);
						showWhenFail(null, false, tempListener);
					}
				} catch(Exception e) {
					Log.i("INFO", "findImage2 ex: " + e.toString());
					showWhenFail(null, false, tempListener);
				}
				
			}
			
		});
		
	}
	
	//调用到此方法时，不用判断，参数可以直接用
	private void requestImagesAndShow(boolean isCensus, List<ImageInfo> imgList, DYAdvertManager.IFindImageListener listener) {
		final IFindImageListener tempListener = listener;
		final List<ImageInfo> tempImgList = imgList;
		final boolean tempIsCensus = isCensus;
		//放入urlKey
		Map<String, String> urlMap = new HashMap<String, String>();
		for(int i = 0; i < imgList.size(); i++) {
			String perId = imgList.get(i).getId();
			String perUrl = imgList.get(i).getUrl();
			if(!checkImageExits(perId, perUrl)) {
				String perSort = imgList.get(i).getSort() + "";
				urlMap.put(perSort, perUrl);
				imgList.get(i).setUrlKey(perSort);
				Log.i("sort", "not exists.." + perUrl);
			} else {
				Log.i("sort", "exists.." + perUrl);
			}
		}
		//dianyou_login_current_id
		if(urlMap.isEmpty()) {
			Map<String, Object> someData = new HashMap<String, Object>();
			someData.put("isCensus", tempIsCensus);
			tempListener.finished(tempImgList, null, someData, true);
			Log.i("sort", "isEmpty..");
			return;
		}
		
		DYHttpUtils.getInstance().encoding("UTF-8").post().urlMap(urlMap).buildMap().execute(new DYBitmapCallback() {
			
			@Override
			public void onResponse(Bitmap response, int id) {
				Log.i("INFO", "onResponse..");
				Activity activity = tempListener.getActivity();
				if(activity != null && activity.isFinishing()) {
					DYHttpUtils.getInstance().cancelTag(id);
					Log.i("INFO", "onResponse cancel:" + id);
				} else {
					Log.i("INFO", "onResponse not cancel:" + id);
				}
			}
			
			@Override
			public void onError(Call call, Exception e, int id) {
				Log.i("INFO", "fail..: " + e.toString());
				Activity activity = tempListener.getActivity();
				if(activity != null && activity.isFinishing()) {
					DYHttpUtils.getInstance().cancelTag(id);
					Log.i("INFO", "onError cancel:" + id);
				} else {
					Log.i("INFO", "onError not cancel:" + id);
				}
			}
			
			
			
			@Override
			public void onResponseMap(Map<String, Bitmap> map) {
				
				//过滤掉空图片
				doListFilter(tempImgList, map);
				
				Map<String, Object> someData = new HashMap<String, Object>();
				someData.put("isCensus", tempIsCensus);
				
				//回调接口
				tempListener.finished(tempImgList, null, someData, true);
				
				//将图片保存到手机中
				saveBitmapToPhone(tempImgList);
				
				Log.i("INFO", "onResponseMap-->" + map.keySet().toString());
				Log.i("INFO", "success");
				
			}
			
			@Override
			public void onErrorMap(Map<String, Call> map) {
				Log.i("INFO", "onErrorMap..");
				showWhenFail(tempImgList, tempIsCensus, tempListener);
			}
		});
	}
	
	
	
	
	@Override
	public void detroy() {
		if(bitmaps != null && !bitmaps.isEmpty()) {
			for(List<Bitmap> listBmp: bitmaps.values()) {
				if(listBmp != null && !listBmp.isEmpty()) {
					for(Bitmap bmp : listBmp) {
						if(bmp != null)
							bmp.recycle();
					}
				}
				listBmp.clear();
			}
			
			bitmaps.clear();
			bitmaps = null;
		}
		
		if(reboundViewPager != null) {
			reboundViewPager.stopAutoGo();
			reboundViewPager = null;
			Log.i("INFO", "destroy autoGo");
		}
		
		dyViewPagerManager = null;
		super.detroy();
	}


	@Override
	protected void showWhenFail(List<ImageInfo> imgInfoList, boolean isCensus, IFindImageListener listener) {
		List<Bitmap> listMap = listener.getFailBitmap();
		if(listMap != null && !listMap.isEmpty()) {
			Map<String, Object> someData = new HashMap<String, Object>();
			someData.put("isCensus", isCensus);
			listener.finished(imgInfoList, listMap, someData, isCensus);
		}
	}

}
