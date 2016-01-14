package com.example.administrator.myproject.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

public final class Settings{
	private static final String DATA_NAME = "jinmajia.dat";

	public enum Field {
		FIRST_INSTALL,
		LAST_REQUEST_DATE,
		UPDATE_SID_DATE,
		LOGIN_INFO_SID,
		LOGIN_INFO_DATA,
		USER_INFO_DATA,
		LOGIN_INFO_ORIGIN_KEY,
		LOGIN_INFO_KEY,
		LOGIN_USER_NMAE,
		LOGIN_PASS,
		LOGIN_STATE,
		IS_GESTURE,//是否开启手势密码
		IS_INVESTINFO,//是否接收投资信息
		IS_NEWSINFO,//是否接收资讯信息
		SHOW_GESTURE,//是否达到显示手势密码的条件
	};
	public static void clearSettingString(Context context, Field field) {
		try {
			Editor sortOrder = context.getSharedPreferences(DATA_NAME,
					Context.MODE_PRIVATE).edit();
			sortOrder.putString(field.name(), "");
			sortOrder.commit();
		} catch (Exception e) {

		}
	}
	public static void clearUserSetting(Context context) {
		
		clearSettingString(context, Field.LOGIN_INFO_KEY);
		clearSettingString(context, Field.LOGIN_INFO_SID);
		clearSettingString(context, Field.LOGIN_PASS);
		clearSettingString(context, Field.LOGIN_USER_NMAE);
		setSettingBoolean(context, Field.LOGIN_STATE, false);
		
	}
	public static int getSettingInt(Context context, Field field,
			int defaultValue) {
		try {
			return context
					.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
					.getInt(field.name(), defaultValue);
		} catch (NullPointerException e) {
			e.printStackTrace();
			return defaultValue;
		}
	}
	
	public static void setSettingInt(Context context, Field field, int value) {
		Editor sortOrder = context.getSharedPreferences(DATA_NAME,
				Context.MODE_PRIVATE).edit();
		sortOrder.putInt(field.name(), value);
		sortOrder.commit();
	}

	public static void setArraylong(Context context, Field field,
			ArrayList<Long> arrayLong) {
		if (arrayLong != null && arrayLong.size() != 0) {
			String s = "";
			for (int i = 0; i < arrayLong.size(); i++) {
				s = s + arrayLong.get(i).toString() + ",";
			}
			Editor sortOrder = context.getSharedPreferences(DATA_NAME,
					Context.MODE_PRIVATE).edit();
			sortOrder.putString(field.name(), s);
			sortOrder.commit();
		}
	}

	public static ArrayList<Long> getArrayLong(Context context, Field field,
			String defaultValue) {
		ArrayList<Long> arrayLong = new ArrayList<Long>();
		String s = context
				.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
				.getString(field.name(), defaultValue);
		if (s != null) {
			String[] ids = s.split(",");
			for (int i = 0; i < ids.length; i++) {
				long id = Long.parseLong(ids[i]);
				arrayLong.add(id);
			}
		}
		return arrayLong;
	}

	public static void setArrayStirng(Context context, Field field,
			ArrayList<String> displayList) {
		String s = "";
		Integer num = 0;
		for (int i = 0; i < displayList.size(); i++) {
			s = s + displayList.get(i) + ",";
			num++;
		}
		Editor sortOrder = context.getSharedPreferences(DATA_NAME,
				Context.MODE_PRIVATE).edit();
		sortOrder.putString(field.name(), s);
		sortOrder.commit();
	}

	public static ArrayList<String> getArrayStirng(Context context,
			Field field, String defaultValue) {
		ArrayList<String> displayList = new ArrayList<String>();
		String s = context
				.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
				.getString(field.name(), defaultValue);
		if (s != null) {
			String[] ids = s.split(",");
			for (int i = 0; i < ids.length; i++) {
				if (!ids[i].equals(""))
					displayList.add(ids[i]);
			}
		}
		return displayList;
	}

	public static String getSettingString(Context context, Field field,
			String defaultValue) {
		try {
			return context
					.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
					.getString(field.name(), defaultValue);
		} catch (Exception e) {

			return defaultValue;
		}

	}

	public static void setSettingString(Context context,  String name,
			String value) {
		try {
			Editor sortOrder = context.getSharedPreferences(DATA_NAME,
					Context.MODE_PRIVATE).edit();
			sortOrder.putString(name, value);
			sortOrder.commit();
		} catch (Exception e) {

		}

	}
	public static String getSettingString(Context context, String name,
			String defaultValue) {
		try {
			return context
					.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
					.getString(name, defaultValue);
		} catch (Exception e) {

			return defaultValue;
		}

	}

	public static void setSettingString(Context context, Field field,
			String value) {
		try {
			Editor sortOrder = context.getSharedPreferences(DATA_NAME,
					Context.MODE_PRIVATE).edit();
			sortOrder.putString(field.name(), value);
			sortOrder.commit();
		} catch (Exception e) {

		}

	}
	public static long getSettingLong(Context context, Field field,
			long defaultValue) {
		try {
			return context
					.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
					.getLong(field.name(), defaultValue);
		} catch (Exception e) {

			return defaultValue;
		}
	}

	public static void setSettingLong(Context context, Field field, long value) {
		try {
			Editor sortOrder = context.getSharedPreferences(DATA_NAME,
					Context.MODE_PRIVATE).edit();
			sortOrder.putLong(field.name(), value);
			sortOrder.commit();
		} catch (Exception e) {

		}

	}

	public static boolean getSettingBoolean(Context context, Field field,
			boolean defaultValue) {
		try {
			return context
					.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
					.getBoolean(field.name(), defaultValue);
		} catch (Exception e) {

			return defaultValue;
		}
	}

	public static void setSettingBoolean(Context context, Field field,
			boolean value) {
		try {
			Editor sortOrder = context.getSharedPreferences(DATA_NAME,
					Context.MODE_PRIVATE).edit();
			sortOrder.putBoolean(field.name(), value);
			sortOrder.commit();
		} catch (Exception e) {

		}
	}

	public static void setSettingObject(Context context, Field field,
			Object object) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String userInfoBase64 = new String(Base64.encode(baos.toByteArray(),
				Base64.NO_WRAP));
		SharedPreferences sharedPre = context.getSharedPreferences(DATA_NAME,
				Context.MODE_PRIVATE);
		Editor editor = sharedPre.edit();
		editor.putString(field.name(), userInfoBase64);
		editor.commit();
	}

	public static Object getSettingObject(Context context, Field field) {
		SharedPreferences sharedPre = context.getSharedPreferences(DATA_NAME,
				Context.MODE_PRIVATE);
		String userInfoBase64 = sharedPre.getString(field.name(), null);

		Object object = null;
		if (object == null && userInfoBase64 != null) {
			byte[] base64Bytes = Base64.decode(userInfoBase64.getBytes(),
					Base64.NO_WRAP);
			ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
			try {
				ObjectInputStream ois = new ObjectInputStream(bais);
				object = (Object) ois.readObject();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return object;
	}

}
