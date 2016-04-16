package com.example.administrator.myproject.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;

public class SDUtil {
	public static final String SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath();

	/**
	 * 检查SD卡是否可用
	 * @return boolean
	 */
	public static boolean isAvailable() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ;
	}

	/**
	 * 读取文件内容
	 * @param path : 相对于sd卡下的路径
	 * @param fileName : 要读取内容的文件名字
	 * @return : 读取到的内容；读取失败，返回null
	 */
	public static String readText(String path , String fileName) {
		if(!isAvailable()) {
			return null;
		}
		String file = SDPATH + File.separator + path + File.separator + fileName;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file)) ;
			StringBuffer buffer = new StringBuffer();
			String line;
			while((line = br.readLine()) != null) {
				buffer.append(line + "\n");
			}
			br.close();
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 写入文件内容
	 * @param path 相对于sd卡下的路径
	 * @param fileName 要写入内容的文件名字
	 * @param content  要写入的文本内容
	 * @param isAppend 是否追加
	 * @return  写入是否成功
	 */
	public static boolean writeText(String path , String fileName , String content , boolean isAppend) {
		if(!isAvailable()) {
			return false;
		}
		File file = new File(SDPATH + File.separator + path);
		if(!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}
		try {
			FileWriter fw = new FileWriter(SDPATH + File.separator + path + File.separator + fileName , isAppend);
			fw.write(content);
			fw.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 读取字节文件内容
	 * @param path 相对于sd卡下的路径
	 * @param fileName 要读取内容的文件名字
	 * @return 文件字节流；失败，返回null
	 */
	public static InputStream readStream(String path , String fileName) {
		if(!isAvailable()) {
			return null;
		}
		try {
			FileInputStream fis = new FileInputStream(SDPATH + File.separator + path + File.separator + fileName);
			return fis;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 读取字节文件内容
	 * @param path 相对于sd卡下的路径
	 * @param fileName 要读取内容的文件名字
	 * @return 文件字节数组；失败，返回null
	 */
	public static byte[] readStreamContent(String path , String fileName) {
		if(!isAvailable()) {
			return null;
		}
		try {
			FileInputStream fis = new FileInputStream(SDPATH + File.separator + path + File.separator + fileName);
			byte[] content = readAllStream(fis);
			fis.close();
			return content;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将字节内容写入文件
	 * @param path 相对于sd卡下的路径
	 * @param fileName 要写入内容的文件名字
	 * @param content  要写入的字节内容
	 * @param isAppend 是否追加
	 * @return  写入是否成功
	 */
	public static boolean writeStream(String path , String fileName , byte[] content , boolean isAppend) {
		if(!isAvailable()) {
			return false;
		}
		File file = new File(SDPATH + File.separator + path);
		if(!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}

		try {
			FileOutputStream fos = new FileOutputStream(SDPATH + File.separator + path + File.separator + fileName , isAppend);
			fos.write(content);
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean writeStream(String path , String fileName , InputStream is , boolean isAppend) {
		if(!isAvailable()) {
			return false;
		}
		File file = new File(SDPATH + File.separator + path);
		if(!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}

		try {
			FileOutputStream fos = new FileOutputStream(SDPATH + File.separator + path + File.separator + fileName , isAppend);
			byte[] content = readAllStream(is);
			if(null != content){
				fos.write(content);
				fos.close();
				return true;
			}
			else {
				fos.close();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将输入流的内容读入到一个byte数组
	 * @param is  输入流
	 * @return    输入流内容构成的byte[]
	 */
	public static byte[] readAllStream(InputStream is) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		byte[] buffer = new byte[1024];
		int size;
		try {
			while((size = is.read(buffer, 0, 1024)) != -1) {
				bos.write(buffer, 0, size);
			}
			byte[] content = bos.toByteArray();
			bos.close();
			return content;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public void fileDelete(String path , String fileName){
		File file = new File(SDPATH + File.separator + path+fileName);
		if(file.exists() ) {
			file.delete();
		}
	}
	/**
	 *
	 * @param suffixes .jpg,.spx....
	 * @param path
	 * @param name
	 * @return
	 */
	public static String getFileName(String suffixes,String path,String name) {
		if (TextUtils.isEmpty(path)){
			path = "cache";
		}
		String saveDir = Environment.getExternalStorageDirectory() +File.separator + path;
		File file = new File(saveDir);
		if (!file.exists()) {
			file.mkdirs();// 创建文件
		}
		// 用日期作为文件名，确保唯一性
		if (TextUtils.isEmpty(name)){
			name = System.currentTimeMillis()+"";
		}
		String fileName = saveDir + File.separator + name + suffixes;
		return fileName;
	}
}
