package com.lwj.springboot_mybatis;

import java.util.HashMap;
import java.util.Map;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Resource {

	@RequestMapping("/path")
	@ResponseBody
	public Map<String, Object> getPath(){
		Map<String, Object> map = new HashMap<String, Object>();
		String path =Thread.currentThread().getContextClassLoader().getResource("").getPath();
		map.put("path", path);
		return map;
	}
}
