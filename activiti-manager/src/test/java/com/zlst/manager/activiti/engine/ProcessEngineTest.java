package com.zlst.manager.activiti.engine;

import static org.junit.Assert.assertNotNull;


import com.zlst.modules.test.spring.SpringTransactionalTestCase;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.cmd.StartProcessInstanceActiveCmd;
import org.activiti.engine.impl.cmd.StartProcessInstanceSuppendCmd;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

/**
 * 测试流程引擎
 *
 * @author HenryYan
 */
@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class ProcessEngineTest extends SpringTransactionalTestCase {

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private FormService formService;

	@Autowired
	private IdentityService identityService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private ManagementService managementService;

	/**
	 * 检测引擎是否能正常工作
	 */
	@Test
	public void testProcessEngines() {
		assertNotNull(repositoryService);
		assertNotNull(runtimeService);
		assertNotNull(formService);
		assertNotNull(identityService);
		assertNotNull(taskService);
		assertNotNull(historyService);
		assertNotNull(managementService);
	}


	/**
	 * 检验流程的实例化和启动
	 */
	@Test
	public void testStartProcessInstance() {
		logger.debug("init start");

		//1.
		//this.runtimeService.startProcessInstanceById("process:2:77516");


		//2.

		String processDefinitionKey = null;
		String processDefinitionId = "process:2:77516";
		String businessKey = null;
		Map<String, Object> variables = null;
		RuntimeServiceImpl runtimeServiceImpl = (RuntimeServiceImpl) runtimeService;
		logger.debug("创建流程实例");
		ProcessInstance processInstance =  runtimeServiceImpl.getCommandExecutor().execute(new StartProcessInstanceSuppendCmd( processDefinitionKey, processDefinitionId, businessKey, variables,""));
		String processInstanceId = processInstance.getProcessInstanceId();

		//this.runtimeService.startProcessInstanceById(processDefinitionId);


		logger.debug("启动流程实例");
		//runtimeServiceImpl.suspendProcessInstanceById(processInstanceId);
		//runtimeServiceImpl.activateProcessInstanceById(processInstanceId);
		processInstance =  runtimeServiceImpl.getCommandExecutor().execute(new StartProcessInstanceActiveCmd( processInstanceId ));

	}

}
