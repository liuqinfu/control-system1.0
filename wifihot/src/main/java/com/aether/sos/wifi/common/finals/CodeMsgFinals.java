package com.aether.sos.wifi.common.finals;

import com.aether.sos.wifi.common.annotation.CodeAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 错误编码
 * @author Administrator
 *
 */
public final class CodeMsgFinals {

	private final static Map<String, String> codeMap = new HashMap<>();
	private static Logger log = LoggerFactory.getLogger(CodeMsgFinals.class);  
	static {
		try {
			Class<?> target = Class.forName("com.aether.sos.wifi.common.finals.CodeFinals");
			Field[] fields = target.getFields();
			for (Field field : fields) {
				boolean isEmpty = field.isAnnotationPresent(CodeAnnotation.class);
				if (isEmpty) {
					Annotation[] annotations = field.getAnnotations();
					for(Annotation annotation:annotations){
		                CodeAnnotation ca = (CodeAnnotation)annotation;
		                codeMap.put(ca.code(), ca.msg());
		            }
				}

			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}
	
	public static String getValue(String key) {
		return codeMap.get(key);
	}
}
