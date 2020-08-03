package com.aether.sos.wifi.common.utils;


import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StringUtil {

	/**
	 * 获取唯一文件名
	 * 
	 * @return
	 * @Author Administrator
	 */
   
	/**
	 * 
	 * @return：JAVA获取36位的GUID字符串串
	 */
	public static String getGUID() {
		return UUID.randomUUID().toString();
	}

	public static String get32GUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static String formatGUID(String miniguid) {
		String a=miniguid.substring(0,8);
		String b=miniguid.substring(8,12);
		String c=miniguid.substring(12,16);
		String d=miniguid.substring(16,20);
		String e=miniguid.substring(20,32);
		miniguid=a+'-'+b+'-'+c+'-'+d+'-'+e;
		return miniguid;
	}


	public static String getUTF8Str(String str) {

		StringBuffer sb = new StringBuffer();
		sb.append(str);
		String xmString = "";
		String strUTF8 = "";
		try {
			xmString = new String(sb.toString().getBytes("UTF-8"));
			strUTF8 = URLEncoder.encode(xmString, "UTF-8");
			//System.out.println("utf-8 编码：" + strUTF8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strUTF8;

	}
	
	public static String getUtf8ByUnicode(String utfString){
		StringBuilder sb = new StringBuilder();
		int i = -1;
		int pos = 0;
		
		while((i=utfString.indexOf("\\u", pos)) != -1){
			sb.append(utfString.substring(pos, i));
			if(i+5 < utfString.length()){
				pos = i+6;
				sb.append((char) Integer.parseInt(utfString.substring(i+2, i+6), 16));
			}
		}
		
		return sb.toString();
	}

	/**
	 * 
	 * @param str：把字符串变成utf8串，如 /AU8V/CCF6
	 * @return
	 */
	public static String toUtf8(String str) {
		try {
			return new String(str.getBytes("UTF-8"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

    /**
     * 
     * @param str:字符串包含{0}{1}这样的，用于把参数拼接进来
     * @param args：无限参数，按顺序拼接到{0},{1}里面
     * @return
     */
	public static String strFormat(String str, Object... args) {

		// 这里用于验证数据有效性
		if (str == null || "".equals(str)) {
			return "";
		}
		if (args.length == 0) {
			return str;
		}
		String result = str;

		// 这里的作用是只匹配{}里面是数字的子字符串
		java.util.regex.Pattern p = java.util.regex.Pattern
				.compile("\\{(\\d+)\\}");
		java.util.regex.Matcher m = p.matcher(str);

		while (m.find()) {
			// 获取{}里面的数字作为匹配组的下标取值
			int index = Integer.parseInt(m.group(1));

			// 这里得考虑数组越界问题，{1000}也能取到值么？？
			if (index < args.length) {

				// 替换，以{}数字为下标，在参数数组中取值
				result = result.replace(m.group(), args[index].toString());
			}
		}
		return result;
	}

	/**
	 * 
	* @Description: 数组转string
	* @date 2015年7月2日 下午2:16:29 
	* @param @param strs
	* @param @return  
	* @author wmd  
	* @version V2.0
	 */
	public static String arrayToString(String[]strs){
		StringBuilder string = new StringBuilder();
		for(String str : strs){
			if(StringUtils.isNotEmpty(str)) {
				string.append(str+',');
			}
		}
		return string.toString();
	}
	
	/**
	 * 
	* @Description: 判断某个字符是不是中文
	* @date 2015年7月2日 下午2:16:47 
	* @param @param oneChar
	* @param @return  
	* @author wmd  
	* @version V2.0
	 */
	public static boolean checkChar(char oneChar){
		if((oneChar >= 0x4e00)&&(oneChar <= 0x9fbb)){  
			return true;
		}
		return false;
	}
	
	/**
	 * 
	* @Description: 判断一段字符串是否是中文
	* @date 2015年7月2日 下午2:19:07 
	* @param @param string
	* @param @return  
	* @author wmd  
	* @version V2.0
	 */
	public static boolean checkChar(String string){
		boolean flag = true;
		for(int i = 0 ;i<string.length();i++){
			char ch = string.charAt(i);
			if(!checkChar(ch)){
				flag = false;
			}
		}
		return flag;
	}
	
	
	/**
	 * 十六进制 转换 byte[]
	 * 
	 * @param hexStr
	 * @return
	 */
	public static byte[] hexString2ByteArray(String hexStr){
		if (hexStr == null) {
			return null;
		}
		if (hexStr.length() % 2 != 0) {
			return null;
		}
		byte[] data = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			char hc = hexStr.charAt(2 * i);
			char lc = hexStr.charAt(2 * i + 1);
			byte hb = hexChar2Byte(hc);
			byte lb = hexChar2Byte(lc);
			if (hb < 0 || lb < 0) {
				return null;
			}
			int n = hb << 4;
			data[i] = (byte) (n + lb);
		}
		return data;
	}
	
	public static byte hexChar2Byte(char c){
		if (c >= '0' && c <= '9') {
			return (byte) (c - '0');
		}
		if (c >= 'a' && c <= 'f') {
			return (byte) (c - 'a' + 10);
		}
		if (c >= 'A' && c <= 'F') {
			return (byte) (c - 'A' + 10);
		}
		return -1;
	}
	
	
	 public static String listToString(List<String> stringList){
	        if (stringList==null) {
	            return null;
	        }
	        StringBuilder result=new StringBuilder();
	        boolean flag=false;
	        for (String string : stringList) {
	            if (flag) {
	                result.append(",");
	            }else {
	                flag=true;
	            }
	            result.append(string);
	        }
	        return result.toString();
	}
	 
	 /**
	  * 字符串转换unicode
	  */
	 public static String string2Unicode(String string) {
	  
	     StringBuffer unicode = new StringBuffer();
	  
	     for (int i = 0; i < string.length(); i++) {
	  
	         // 取出每一个字符
	         char c = string.charAt(i);
	  
	         // 转换为unicode
	         unicode.append("\\u" + Integer.toHexString(c));
	     }
	  
	     return unicode.toString();
	 }

	public static String randomStr(int length) {
		int i = length;//控制字符长度
		StringBuilder sb = new StringBuilder() ;
		for (int j = 0; j < i; j++) {
			//生成一个97-122之间的int类型整数--为了生成小写字母
			int intValL = (int)(Math.random()*26+97);
			//生成一个65-90之间的int类型整数--为了生成大写字母
			int intValU = (int)(Math.random()*26+65);
			//生成一个30-39之间的int类型整数--为了生成数字
			int intValN = (int)(Math.random()*10+48);

			int intVal = 0;
			int r = (int)(Math.random()*3);

			if(r==0) {
				intVal = intValL;
			}else if (r==1) {
				intVal = intValU;
			}else {
				intVal = intValN;
			}

			sb.append((char) intVal);
		}
		return sb.toString();

	}
	
	public static void main(String[] args) {
		List<String> list=new ArrayList<String>();
        list.add("aaa");
        list.add("bbb");
        list.add("ccc");
        System.out.println(listToString(list));//aaa,bbb,ccc
	}

}
