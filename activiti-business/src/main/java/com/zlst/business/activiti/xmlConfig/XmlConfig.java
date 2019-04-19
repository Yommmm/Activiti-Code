package com.zlst.business.activiti.xmlConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by songcj on 2017/8/1.
 * 该类必须存在，预先导入配置文件，否则xml不生效
 */
@Configuration
@ImportResource(locations={"classpath:beans/*.xml"})
public class XmlConfig {
}
