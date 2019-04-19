package com.zlst.business.activiti.exception;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.impl.ActivitiEntityExceptionEventImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.EventLogEntryEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

/**
 * Created by user00 on 2017/7/27.
 */
@Service
@Transactional
public class JobExceptionListener implements ActivitiEventListener {

    protected Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public void onEvent(ActivitiEvent event) {
        switch (event.getType()) {

            case JOB_EXECUTION_FAILURE:

                try{
                    ActivitiEntityExceptionEventImpl entityEvent = (ActivitiEntityExceptionEventImpl)event;
                    EventLogEntryEntity eventLogEntry = new EventLogEntryEntity();

                    Connection connection = Context.getCommandContext().getProcessEngineConfiguration().getDataSource().getConnection();
                    PreparedStatement ps = connection.prepareStatement("insert into ACT_EVT_LOG(TYPE_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, TIME_STAMP_,DATA_ ) values ( ?, ?, ?, ?, ?,?) ");
                    ps.setString(1,event.getType().toString());
                    ps.setString(2,event.getProcessDefinitionId());
                    ps.setString(3,event.getProcessInstanceId());
                    ps.setString(4,event.getExecutionId());
                    ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                    StringBuffer errorStacks = new StringBuffer(entityEvent.getCause().toString() + "\n");
                    if (entityEvent.getCause().getStackTrace() != null && entityEvent.getCause().getStackTrace().length > 0){
                        StackTraceElement[] stacks =  entityEvent.getCause().getStackTrace();

                        for(int i = 0; i < stacks.length;i++ ){
                            StackTraceElement statck = stacks[i];
                            errorStacks.append(statck.toString() + "\n");
                        }
                    }

                    ps.setBytes(6,errorStacks.toString().getBytes());
                    ps.execute();
                    System.out.println("getProcessDefinitionId: " + eventLogEntry.getProcessDefinitionId() + " event.getType().toString():" + event.getType().toString() );
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                break;

            default:
                System.out.println("Event received: " + event.getType());
        }
    }


    @Override
    public boolean isFailOnException() {
        return false;
    }
}
