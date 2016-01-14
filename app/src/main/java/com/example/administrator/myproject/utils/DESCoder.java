package com.example.administrator.myproject.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.util.Base64;

public class DESCoder {

	private static String ALGORITHM = "DES";
	/*
	* @param encryptString 待加密的字符串
	* @param encryptKey 生成秘钥的字符串，例如：机器码   但是只能是8位
	* @return 加密后的字符串
	*/
	public static String encrypt(String encryptString,String encryptKey){   
		try{
			SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(),ALGORITHM);
		    Cipher cipher = Cipher.getInstance(ALGORITHM);
		    cipher.init(Cipher.ENCRYPT_MODE,key);//Cipher.ENCRYPT_MODE（加密标识）
		    byte[] encryptedData = cipher.doFinal(encryptString.getBytes("UTF-8"));//加密   
		    return new String(Base64.encode(encryptedData,Base64.DEFAULT));//Base64加密生成在Http协议中传输的字符串
		}catch(Exception e){
			
		}
		return null;
	} 
	
	/*
	* @param decryptString 待解密的字符串
	* @param decryptKey 生成秘钥的字符串，例如：机器码 但是只能是8位
	* @return 解密后的字符串
	*/ 
	public static String decrypt(String decryptString, String decryptKey) {
		try{
			byte[] byteMi = Base64.decode(decryptString,Base64.DEFAULT);
		    SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(),ALGORITHM); 
		    Cipher cipher = Cipher.getInstance(ALGORITHM);
		    cipher.init(Cipher.DECRYPT_MODE,key);
		    byte[] decryptData = cipher.doFinal(byteMi);
		    return new String(decryptData);
		}catch(Exception e){
			
		}
		return null;
	}  
	
	
//	public static void main(String args[]){
//		String str = "fc14616801aa29771b63ded44ccbe5a9";
//		String key =  "9036C50A";
		
//		String str = "{\"password\":\"a1b2c3\",\"username\":\"test\"}";
//		String key =  "6349661F";
//		try {
//			System.out.println(System.currentTimeMillis());
//			String s = encrypt(str,key);
//			System.out.println(s);
//			s = encrypt("{\"dealPass\":\"12345678\"}",key);
//			System.out.println(s);
//			System.out.println(URLEncoder.encode(s, "UTF-8"));
			
//			System.out.println(decrypt("o/q22geauJxq1UMBgJ9z6EfIEdQ7inlOGTGkHFsyDDFNQ9SUXwBIdQ5rQ40ZLxdudNucHU4ZnHyWaJc9thrHBWwmK9Fkxcfh/JdAqnvI5BNuHc0ZLYvYMMvnEBPwWS40Ha5i2lagWX4pz5Ik/MuShserhPNyuM0kfk3U0OokT5UwC2TmFXbKQg==",key));
			
//			System.out.println(decrypt("Zy38IkKT9h40WnwY6P3Peg9zRCkSRNG3Zo+UHlllF907LK8hMnkz4Cd4R1OtAyAQfU25jDpNidc5XUnBUv1zKuJWwFtsHKDuvUjUor0jDNUUaWXOOTBv2PAr7+4LlLrQt1t0pBT7+F/ZTDqEDjvA+K+xklD6QF19Cv7tbr/qvA/DkzLfKrhg8XrgxVyh5gq348odi8trUn4RCqLyP26yaSXS+QrZOcQeTnHC8A1g9KDcbhd+WvIzpQ==", key));
//			System.out.println(java.net.URLEncoder.encode("Dwg+zqgX7K5zvLjTK4oWSpd6J9Z27a3hI12Egiqt8BIgl6TwaPt5CmZYFscR0VJ1", "UTF-8"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
}
