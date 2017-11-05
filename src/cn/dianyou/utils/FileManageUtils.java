package cn.dianyou.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

public class FileManageUtils {
	
	public static final int BIT_SIZE = 0X000f;
	public static final int KIB_SIZE = 0X00ff;
	public static final int MIB_SIZE = 0X0fff;
	
	
	//判断sd卡是否可以被访问
	public static boolean isAccessSD() {
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			if(Environment.getExternalStorageDirectory().canWrite()) {
				return true;
			}
		}
		return false;
	}
	
	//计算sd卡剩余空间大小 默认单位:bit
	public static long getAvailableSizeSD(int sizeUnit) {
		float perRadius = 1.0f;
		switch(sizeUnit) {
		case BIT_SIZE:
			perRadius = 1.0f;
			break;
		case KIB_SIZE:
			perRadius = 1.0f/1024.0f;
			break;
		case MIB_SIZE:
			perRadius = 1.0f/1024.0f/1024.0f;
			break;
		default:
		}
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return (long) (availableBlocks * blockSize * perRadius);
	}
	
	public static long getAvailableSizeSD() {
		return getAvailableSizeSD(BIT_SIZE);
	}
	
	//获取sd卡总大小
	public static long getTotalSizeSD(int sizeUnit) {
		float perRadius = 1.0f;
		switch(sizeUnit) {
		case BIT_SIZE:
			perRadius = 1.0f;
			break;
		case KIB_SIZE:
			perRadius = 1.0f/1024.0f;
			break;
		case MIB_SIZE:
			perRadius = 1.0f/1024.0f/1024.0f;
			break;
		default:
		}
		
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long blockCount = stat.getBlockCount();
		return (long) (blockSize * blockCount * perRadius);
	}
	
	public static long getTotalSizeSD() {
		return getTotalSizeSD(BIT_SIZE);
	}
	
	public static boolean isHasSpareSpaceSD(int sizeUnit, long spaceSize) {
		long needSize = spaceSize;
		
		long remandSpace = getAvailableSizeSD(sizeUnit);
		
		return remandSpace >= needSize; 
	}
	
	public static String writeToPathSD(Bitmap bmp, String filedir, String filename) {
		File sdDirFile = Environment.getExternalStorageDirectory();
		String sdDirPath = sdDirFile.getPath();
		if(!sdDirPath.endsWith("/")) {
			sdDirPath += "/";
		}
		
		File dir = new File(sdDirPath + filedir);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		return writeToPath(bmp, dir, filename);
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static String writeToPath(Bitmap bmp, File dirFile, String filename) {
		if(bmp == null || dirFile == null) return null;
		
		if(dirFile.isFile()) {
			if(filename == null || filename.trim().equals("")) {
				filename = getFileName(dirFile.getPath());
				Log.i("INFO", "filename....:" + filename);
			}
			
		}
		
		if(filename == null || filename.trim().equals("")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			String dateFor = sdf.format(new Date());
			filename = OtherUtils.getHex_13() + "_" + dateFor + "_" + System.currentTimeMillis()  + ".jpg";
		} else {
			if(!filename.contains(".")) 
				filename += ".jpg";
		}
		
		
		File dirF = new File(getFileParent(dirFile.getPath()));
		Log.i("INFO", "parent:" + dirF.getPath());
		if(!dirF.exists()) {
			dirF.mkdirs();
		}
		
		
		File f = new File(dirF, filename);
		
		CompressFormat bcf = Bitmap.CompressFormat.JPEG;
		String bcfFormat = filename.substring(filename.indexOf(".") + 1).toLowerCase();
		if(bcfFormat.equals("jpg") || bcfFormat.equals("jpeg")) {
			bcf = Bitmap.CompressFormat.JPEG;
		} else if(bcf.equals("png")) {
			bcf = Bitmap.CompressFormat.PNG;
		} else if(bcf.equals("webp")) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				bcf = Bitmap.CompressFormat.WEBP;
		}
		
		
		try {
			boolean isSuccess = bmp.compress(bcf, 100, new FileOutputStream(f));
			if(isSuccess) {
				return f.getPath();
			}
		} catch (FileNotFoundException e) {
		}
		return null;
	}
	
	public static String getFileName(String filepath) {
		if(filepath == null) return "";
		
		if(filepath.endsWith("/")) {
			filepath = filepath.substring(0, filepath.length() - 1);
		}
		
		int _index = filepath.lastIndexOf("/");
		if(_index == -1) {
			return filepath;
		} else {
			return filepath.substring(_index + 1, filepath.length());
		}
	}
	
	public static String getFileParent(String filepath) {
		if(filepath == null) return "";
		
		if(filepath.endsWith("/")) {
			filepath = filepath.substring(0, filepath.length() - 1);
		}
		
		int _index = filepath.lastIndexOf("/");
		if(_index == -1 || !filepath.substring(_index).contains(".")) {
			return filepath;
		} else {
			Log.i("INFO", "..bbbbbb");
			return filepath.substring(0, _index);
		}
	}
	
	public static boolean isExistsFile(String filepath) {
		if(filepath == null || filepath.trim().equals("")) 
			return false;
		
		File file = new File(filepath);
		return file.exists();
	}
	
	private static String clearDirName(String dirname) {
		while(dirname.startsWith("/")) {
			if(dirname.length() >= 2)
				dirname = dirname.substring(1);
			else 
				dirname = "";
		}
		
		while(dirname.endsWith("/")) {
			dirname = dirname.substring(0, dirname.lastIndexOf("/"));
		}
		
		while(dirname.contains(".")) {
			dirname = dirname.substring(0, dirname.lastIndexOf("."));
		}
		
		return dirname;
	}
	
	public static boolean isExistsFile_b2(Context context, String dirname, String filename) {
		return isExistsFile_p2(context, dirname, filename) != null;
	}
	
	
	
	public static String isExistsFile_p2(Context context, String dirname, String filename) {
		if(filename == null || filename.trim().equals("") || !filename.contains(".")) return null;
		
		if(dirname == null) dirname = "";
		
		if(!dirname.trim().equals("")) {
			dirname = clearDirName(dirname);
		}
		
		String resultPath = "";
		
		if(isAccessSD()) {
			resultPath = Environment.getExternalStorageDirectory().getPath();
		} else {
			if(context == null) return null;
			resultPath =  context.getFilesDir().getPath();
		}
		
		if(!resultPath.endsWith("/")) {
			resultPath += "/"; 
		}
		
		if(dirname.trim().equals("")) {
			resultPath += filename;
		} else {
			resultPath = resultPath + dirname + "/" + filename;
		}
		
		File pF = new File(resultPath);
		if(pF.exists()) {
			return pF.getPath();
		}
		
		return null;
	}
	
	public static String isExistsDir_p2(Context context, String dirname) {
		if(dirname == null || dirname.trim().equals("")) return null;
		dirname = clearDirName(dirname);
		
		String resultPath = "";
		
		if(isAccessSD()) {
			resultPath = Environment.getExternalStorageDirectory().getPath();
		} else {
			if(context == null) return null;
			resultPath =  context.getFilesDir().getPath();
		}
		
		if(!resultPath.endsWith("/")) {
			resultPath += "/"; 
		}
		
		resultPath += dirname;
		
		File f = new File(resultPath);
		if(f.exists()) {
			return f.getPath();
		}
		
		return null;
	}
	
	public static boolean isExistsDir_b2(Context context, String dirname) {
		return isExistsDir_p2(context, dirname) != null;
	}
	
	public static boolean isExistsFile(File file) {
		if(file == null) 
			return false;
		return file.exists();
	}
	
	public static long getTotalSizeInternal(int sizeUnit) {
		float perRadius = 1.0f;
		switch(sizeUnit) {
		case BIT_SIZE:
			perRadius = 1.0f;
			break;
		case KIB_SIZE:
			perRadius = 1.0f/1024.0f;
			break;
		case MIB_SIZE:
			perRadius = 1.0f/1024.0f/1024.0f;
			break;
		default:
		}
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return (long) (totalBlocks * blockSize * perRadius);
	}
	
	public static long getTotalSizeInternal() {
		return getTotalSizeInternal(BIT_SIZE);
	}
	
	public static long getAvailableSizeInternal(int sizeUnit) {
		float perRadius = 1.0f;
		switch(sizeUnit) {
		case BIT_SIZE:
			perRadius = 1.0f;
			break;
		case KIB_SIZE:
			perRadius = 1.0f/1024.0f;
			break;
		case MIB_SIZE:
			perRadius = 1.0f/1024.0f/1024.0f;
			break;
		default:
		}
		File path = Environment.getDataDirectory();  
	    StatFs stat = new StatFs(path.getPath());  
	    long blockSize = stat.getBlockSize();  
	    long availableBlocks = stat.getAvailableBlocks();  
	    return (long) (availableBlocks * blockSize * perRadius);
	}
	
	public static long getAvailableSizeInternal() {
		return getAvailableSizeInternal(BIT_SIZE);
	}
	
	public static boolean isHasSpareSpaceInteranal(int sizeUnit, long spaceSize) {
		long needSize = spaceSize;
		
		long remandSpace = getAvailableSizeInternal(sizeUnit);
		
		return remandSpace >= needSize; 
	}
	
	
	public static long getBitmapSize(Bitmap bmp, int sizeUnit) {
		if(bmp == null)
			return 0;
		float perRadius = 1.0f;
		switch(sizeUnit) {
		case BIT_SIZE:
			perRadius = 1.0f;
			break;
		case KIB_SIZE:
			perRadius = 1.0f/1024.0f;
			break;
		case MIB_SIZE:
			perRadius = 1.0f/1024.0f/1024.0f;
			break;
		default:
		}
		return (long) (bmp.getRowBytes() * bmp.getHeight() * perRadius);
	}
	
	
	public static String saveBitmap(Context context, Bitmap bmp, String dirname, String filename) {
		long bmpSize = getBitmapSize(bmp, BIT_SIZE);
		if(isAccessSD()) {
			if(isHasSpareSpaceSD(BIT_SIZE, bmpSize)) {
				return writeToPathSD(bmp, dirname, filename);
			} else {
				if(isHasSpareSpaceInteranal(BIT_SIZE, bmpSize)) {
					return writeToPathInternal(context, bmp, dirname, filename);
				} else {
					Toast.makeText(context, "手机资源不足，请清理内存！", Toast.LENGTH_LONG).show();
				}
			}
		} else {
			if(isHasSpareSpaceInteranal(BIT_SIZE, bmpSize)) {
				return writeToPathInternal(context, bmp, dirname, filename);
			} else {
				Toast.makeText(context, "手机资源不足，请清理内存！", Toast.LENGTH_LONG).show();
			}
		}
		return null;
	}
	
	public static String saveBitmapSD(Bitmap bmp, String dirname, String filename) {
		long bmpSize = getBitmapSize(bmp, BIT_SIZE);
		if(isAccessSD()) {
			if(isHasSpareSpaceSD(BIT_SIZE, bmpSize)) {
				return writeToPathSD(bmp, dirname, filename);
			}
		}
		return null;
	}
	
	public static String writeToPathInternal(Context context, Bitmap bmp, String dirname, String filename) {
		if(bmp == null) return null;
		File dirFile = context.getFilesDir();
		String dirPath = dirFile.getPath();
		if(!dirPath.endsWith("/")) {
			dirPath += "/";
		}
		
		File dir = new File(dirPath + dirname);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		return writeToPath(bmp, dir, filename);
	}
	
	public static Bitmap obtainBitmap(Context context, String dirname, String filename) {
		String path = isExistsFile_p2(context, dirname, filename);
		if(path == null)
			return null;
		return BitmapFactory.decodeFile(path);
	}
	
	
	public static ArrayList<File> deleteFilesAtDir(Context context, String dirname, int limitCount, int deleteCount) {
		String dirpath = isExistsDir_p2(context, dirname);
		return deleteFilesAtPath(context, dirpath, limitCount, deleteCount);
	}
	
	public static ArrayList<File> deleteFilesAtPath(Context context, String dirPath, int limitCount, int deleteCount) {
		
		final ArrayList<File> deleteList = new ArrayList<File>();
		
		if(dirPath == null || !new File(dirPath).exists() || deleteCount <= 0) return deleteList;
		
		LinkedList<File> allList = new LinkedList<File>();
		File dirFile = new File(dirPath);
		if(dirFile.isFile()) {
			dirFile.delete();
			deleteList.add(dirFile);
			return deleteList;
		}
		
		final ArrayList<File> allFiles = getFilesAtDir(dirPath);
		final int totalCount = allFiles.size();
		
		Log.i("sort", "totalCount:" + totalCount + ", limitCount:" + limitCount);
		
		if(limitCount >= totalCount) {
			return deleteList;
		}
		
		if(deleteCount > totalCount) {
			deleteCount = totalCount;
		}
		
		allList.addAll(allFiles);
		
		Collections.sort(allList, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				if(o1.lastModified() == o2.lastModified())
					return 0;
				return o1.lastModified() > o2.lastModified() ? 1 : -1;
			}
			
		});
		
		for(File file : allList) {
			Log.i("sort", file.getName() + ": " + TimeMgUtils.getTimeStrByMillis(file.lastModified()));
		}
		
		for(int i = 0; i < deleteCount; i++) {
			File popFile = allList.removeFirst();
			boolean isSuccess = popFile.delete();
			if(isSuccess) {
				File parentFile = popFile.getParentFile();
				boolean deleteFlag = true;
				while(parentFile != null && deleteFlag && parentFile.list().length <= 0) {
					if(parentFile.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath()) || parentFile.getAbsolutePath().equals(context.getFilesDir().getAbsolutePath())) {
						break;
					}
					deleteFlag = parentFile.delete();
					parentFile = parentFile.getParentFile();
				}
				deleteList.add(popFile);
			}
			
			Log.i("sort", "pop:" + popFile.getName());
		}
		
		System.out.println("---------------------");
		
		for(File file : allList) {
			Log.i("sort", file.getName() + ": " + TimeMgUtils.getTimeStrByMillis(file.lastModified()));
		}
		
		return deleteList;
		
	}
	
	//@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static ArrayList<File> getFilesAtDir(String dirPath) {
		if(dirPath == null) return new ArrayList<File>();
		
		File dir = new File(dirPath);
		if(!dir.exists()) {
			return new ArrayList<File>();
		}
		
		if(dir.isFile()) return new ArrayList<File>(Arrays.asList(new File[]{dir}));
		
		/*File[] files = dir.listFiles();
		
		File[] allFiles = new File[0]*/;
		
		File[] files = dir.listFiles();
		ArrayList<File> allFiles = new ArrayList<File>();
		
		for(File file : files) {
			if(file.isDirectory()) {
				/*File[] newFiles = getFilesAtDir(file.getPath());
				int oldLength = allFiles.length;
				int newLength = oldLength + newFiles.length;
				allFiles = Arrays.copyOf(allFiles, newLength);
				System.arraycopy(newFiles, 0, allFiles, oldLength, newFiles.length);*/
				allFiles.addAll(getFilesAtDir(file.getPath()));
			} 
			
			if(file.isFile()) {
				/*allFiles = Arrays.copyOf(allFiles, allFiles.length + 1);
				allFiles[allFiles.length - 1] = file;*/
				allFiles.add(file);
			}
		}
		
		return allFiles;
	}
	
}
