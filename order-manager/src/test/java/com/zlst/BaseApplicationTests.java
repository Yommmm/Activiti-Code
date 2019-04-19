package com.zlst;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import com.zlst.utils.GsonUtil;
import org.junit.*;

/**
 * 测试用例基类，用于MVC测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZlstApplication.class)
//@Transactional //打开的话测试之后数据可自动回滚
public class BaseApplicationTests extends MockMvcResultMatchers {

	@Autowired
	WebApplicationContext webApplicationContext;

	protected MockMvc mockMvc;
	
	private GsonUtil utils;


	@Before
	public void setUp() throws Exception {
//		mockMvc = Mo ckMvcBuilders.standaloneSetup(new DemoCtrl()).build();
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void tt(){
		utils.parseJsonWithGson(null,null);
	}

	protected String buildGetReq(String path,MultiValueMap<String, String> params) throws Exception{

		if (params==null){
			return mockMvc.perform(MockMvcRequestBuilders.get(path)).andReturn().getResponse().getContentAsString();
		}else{
			return mockMvc.perform(MockMvcRequestBuilders.get(path).params(params)).andReturn().getResponse().getContentAsString();
		}
	}

	protected String buildPostReq(String path,MultiValueMap<String, String> params) throws Exception{
		if (params==null){
			return mockMvc.perform(MockMvcRequestBuilders.post(path)).andReturn().getResponse().getContentAsString();
		}else{
			return mockMvc.perform(MockMvcRequestBuilders.post(path).params(params)).andReturn().getResponse().getContentAsString();
		}
	}

	protected String buildDelReq(String path,MultiValueMap<String, String> params) throws Exception{
		if (params==null){
			return mockMvc.perform(MockMvcRequestBuilders.delete(path)).andReturn().getResponse().getContentAsString();
		}else{
			return mockMvc.perform(MockMvcRequestBuilders.delete(path).params(params)).andReturn().getResponse().getContentAsString();
		}
	}

	/**
	 * 断言代码片断
	 * @throws Exception
	 */
//	mockMvc.perform(request)
//				.andExpect(status().isOk())
//				.andExpect(content().string(equalTo("[{\"id\":1,\"name\":\"测试大师\",\"age\":20}]")));

}
