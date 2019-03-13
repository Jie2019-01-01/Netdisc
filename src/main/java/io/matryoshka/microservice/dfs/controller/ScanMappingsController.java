package io.matryoshka.microservice.dfs.controller;

import java.util.Collection;
import java.util.List;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;

import io.matryoshka.microservice.dfs.bean.Resource;
import io.matryoshka.microservice.dfs.serviec.ScanMappingsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;

@Controller
@Api("服务之间角色与资源绑定")
@RestSchema(schemaId="scanResource")
@RequestMapping(path="/")
public class ScanMappingsController {

	@Autowired
	private ScanMappingsService scanMappingsService;
	
	@ResponseBody
	@GetMapping(value="/resourceRoleBinding")
	@ApiOperation(value = "返回资源到服务端")
	public Collection<Resource> resourceRoleBinding(){
		Collection<Resource> collection = scanMappingsService.doScan();
		return collection;
	}
}
