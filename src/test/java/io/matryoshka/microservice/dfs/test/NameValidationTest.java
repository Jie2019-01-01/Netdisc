package io.matryoshka.microservice.dfs.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

import io.matryoshka.microservice.dfs.test.bean.SourceBean;
import io.matryoshka.microservice.dfs.bean.UserRoleCatalogBean;

public class NameValidationTest {

	@Value("${project.version}")
	private String version;
	
	public static void main(String[] args) {
//		boolean b = NameValidation.isContainChinese("123");
//		System.out.println(b);
		
	}
	
    public Map<Integer,List<SourceBean>> aaa(){
    	Map<Integer, List<SourceBean>> map = new HashMap<Integer, List<SourceBean>>();
    	
    	List<SourceBean> lsbean1 = new ArrayList<SourceBean>();
    	
    	SourceBean sb1 = new SourceBean();      sb1.setSourcePath("aaaaaaaaaa");
		SourceBean sb2 = new SourceBean();		sb2.setSourcePath("bbbbbbbbbb");
		lsbean1.add(sb1);		lsbean1.add(sb2);
		map.put(6, lsbean1);
		
		List<SourceBean> lsbean2 = new ArrayList<SourceBean>();
		SourceBean sb3 = new SourceBean();		sb3.setSourcePath("cccccccccc");
		SourceBean sb4 = new SourceBean();		sb4.setSourcePath("dddddddddd");
		lsbean2.add(sb3);		lsbean2.add(sb4);
		map.put(2, lsbean2);
		
		List<SourceBean> lsbean3 = new ArrayList<SourceBean>();
		SourceBean sb5 = new SourceBean();		sb5.setSourcePath("eeeeeeeeee");
		SourceBean sb6 = new SourceBean();		sb6.setSourcePath("ffffffffff");
		lsbean3.add(sb5);		lsbean3.add(sb6);
		map.put(3, lsbean3);
				
		Set<Integer> roleids = map.keySet();
		for (Integer roleid : roleids) {
			System.out.println("角色"+roleid+" : "+map.get(roleid));
		}
		
		return map;
    }
    
    public List<UserRoleCatalogBean> bbb(){
    	
    	List<UserRoleCatalogBean> l = new ArrayList<>();
    	
    	UserRoleCatalogBean urcbean1 = new UserRoleCatalogBean();
    	urcbean1.setRoleid(1);	urcbean1.setCid(1);
    	l.add(urcbean1);
    	
    	UserRoleCatalogBean urcbean2 = new UserRoleCatalogBean();
    	urcbean2.setRoleid(2);	urcbean2.setCid(2);
    	l.add(urcbean2);
    	
    	UserRoleCatalogBean urcbean3 = new UserRoleCatalogBean();
    	urcbean3.setRoleid(3);	urcbean3.setCid(3);
    	l.add(urcbean3);

    	for(UserRoleCatalogBean urcbean: l){
    		System.out.println("角色id==="+urcbean.getRoleid()+"\t目录id === "+urcbean.getCid());
    	}
    	
    	return l;
    }
}
