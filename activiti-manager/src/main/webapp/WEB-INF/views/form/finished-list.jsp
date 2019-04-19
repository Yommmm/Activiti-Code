<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html lang="en">
<head>
	<%@ include file="/common/global.jsp"%>
	<title>已结束列表</title>
	<%@ include file="/common/meta.jsp" %>
    <%@ include file="/common/include-base-styles.jsp" %>
    <%@ include file="/common/include-jquery-ui-theme.jsp" %>
    <link href="${ctx }/js/common/plugins/jui/extends/timepicker/jquery-ui-timepicker-addon.css" type="text/css" rel="stylesheet" />
    <link href="${ctx }/js/common/plugins/qtip/jquery.qtip.min.css" type="text/css" rel="stylesheet" />
    <%@ include file="/common/include-custom-styles.jsp" %>
	<script src="${ctx }/js/common/jquery-1.8.3.js" type="text/javascript"></script>
	<script src="${ctx }/js/common/plugins/jui/jquery-ui-${themeVersion }.min.js" type="text/javascript"></script>
	<script src="${ctx }/js/common/plugins/jui/extends/timepicker/jquery-ui-timepicker-addon.js" type="text/javascript"></script>
	<script src="${ctx }/js/common/plugins/jui/extends/i18n/jquery-ui-date_time-picker-zh-CN.js" type="text/javascript"></script>
	<script src="${ctx }/js/common/plugins/qtip/jquery.qtip.pack.js" type="text/javascript"></script>
	<script src="${ctx }/js/common/plugins/html/jquery.outerhtml.js" type="text/javascript"></script>
	<script src="${ctx }/js/module/activiti/workflow.js" type="text/javascript"></script>
	<script type="text/javascript">
        $(function() {
            // 跟踪
            $('.trace').click(graphTrace);
        });
	</script>
</head>

<body>
	<table>
		<tr>
			<th>流程ID</th>
			<th>流程定义ID</th>
			<th>流程启动时间</th>
			<th>流程结束时间</th>
			<th>流程结束原因</th>
			<th>流程轨迹查看</th>
		</tr>

		<c:forEach items="${page.result }" var="hpi">
		<tr>
			<td>${hpi.id }</td>
			<td>${hpi.processDefinitionId }</td>
			<td>${hpi.startTime }</td>
			<td>${hpi.endTime }</td>
			<td>${empty hpi.deleteReason ? "正常结束" : hpi.deleteReason}</td>
			<td><a class="trace" href='#' pid="${hpi.id }" pdid="${hpi.processDefinitionId}" title="点击查看流程图">流程轨迹查看</a></td>
		</tr>
		</c:forEach>
	</table>
	<tags:pagination page="${page}" paginationSize="${page.pageSize}"/>
	<!-- 办理任务对话框 -->
	<div id="handleTemplate" class="template"></div>

</body>
</html>
