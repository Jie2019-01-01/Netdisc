package io.matryoshka.microservice.dfs.serviec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import io.matryoshka.microservice.dfs.bean.CatalogManagerBean;
import io.matryoshka.microservice.dfs.bean.Resource;
import io.swagger.annotations.ApiOperation;

@Service
public class ScanMappingsService {
	
	private String[] emptyArray = new String[]{""};
	@Autowired
	private RequestMappingHandlerMapping handlerMapping;
	
	
	@PostConstruct
	public Collection<Resource> doScan() {
		 return handlerMapping.getHandlerMethods()
		.values()
		.stream()
		.map(this::getResources)
		.flatMap(Collection::stream)
		.collect(Collectors.toList());
	}
    /**
     * 获取Resource
     *
     * @param handlerMethod
     * @return
     */
    public List<Resource> getResources(HandlerMethod handlerMethod ) {
			RequestMapping requestMappingAnnotation = handlerMethod.getBeanType().getAnnotation(RequestMapping.class);
	        RequestMapping methodMappingAnnotation = handlerMethod.getMethodAnnotation(RequestMapping.class);
	        if (Objects.isNull(requestMappingAnnotation) && Objects.isNull(methodMappingAnnotation)) {
	            return Collections.emptyList();
	        }
	        ApiOperation apiOperation = handlerMethod.getMethodAnnotation(ApiOperation.class);
	        String[] requestMappings = Objects.nonNull(requestMappingAnnotation) ? requestMappingAnnotation.value() : emptyArray;
	        String[] methodMappings = Objects.nonNull(methodMappingAnnotation) ? methodMappingAnnotation.path() : emptyArray;
	        RequestMethod[] method = Objects.nonNull(methodMappingAnnotation) ? methodMappingAnnotation.method() : new RequestMethod[0];
	        requestMappings = requestMappings==null?emptyArray : requestMappings;
	        methodMappings = methodMappings==null? emptyArray : methodMappings;
	        Set<String> mappings = new HashSet<>(1);
	        for (String reqMapping : requestMappings) {
	            for (String methodMapping : methodMappings) {
	                mappings.add(reqMapping + methodMapping);
	            }
	        }
	        List<Resource> resources = new ArrayList<>(1);
	        for (RequestMethod requestMethod : method) {
	            for (String mapping : mappings) {
	                //接口描述
	                Resource resource = new Resource();
	                resource.setResourceName(Objects.nonNull(apiOperation) ? apiOperation.value() : "未命名资源路径");
	                resource.setMapping(mapping);
	                resource.setMethod(requestMethod.name());
	                resource.setAuthType(2);
	                resource.setPerm(new Resource().getResourcePermTag(requestMethod.name(), mapping));
	                resource.setResourceType("netdisc");
	                resources.add(resource);
	            }
	        }
	        return resources;
		} 
}
