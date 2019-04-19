<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html lang="en">
<head>
	<%@ include file="/common/global.jsp"%>
	<title>登录页</title>
	<script>
		var logon = ${not empty user};
		if (logon) {
			location.href = '${ctx}/main/index';
		}
	</script>
	<%@ include file="/common/meta.jsp" %>
	<%@ include file="/common/include-jquery-ui-theme.jsp" %>
    <%@ include file="/common/include-base-styles.jsp" %>
    <style type="text/css">
        .login-center {
            width: 600px;
            margin-left:auto;
            margin-right:auto;
        }
        #loginContainer {
            margin-top: 3em;
        }
        .login-input {
            padding: 4px 6px;
            font-size: 14px;
            vertical-align: middle;
        }
    </style>

    <script src="${ctx }/js/common/jquery-1.8.3.js" type="text/javascript"></script>
    <script src="${ctx }/js/common/plugins/jui/jquery-ui-${themeVersion }.min.js" type="text/javascript"></script>
    <script type="text/javascript">
	$(function() {
		$('button').button({
			icons: {
				primary: 'ui-icon-key'
			}
		});
	});
	</script>
</head>

<body>
    <div id="loginContainer" class="login-center">
        <c:if test="${not empty param.error}">
            <h2 id="error" class="alert alert-error">用户名或密码错误！！！</h2>
        </c:if>
        <c:if test="${not empty param.timeout}">
            <h2 id="error" class="alert alert-error">未登录或超时！！！</h2>
        </c:if>

		<div style="text-align: center;">
            <h2>工作流引擎Activiti</h2>
		</div>
		<hr />
		<form action="${ctx }/user/logon" method="get">
			<table>
				<tr>
					<td width="200" style="text-align: right;">用户名：</td>
					<td><input id="username" name="username" class="login-input" placeholder="用户名（见下左表）" /></td>
				</tr>
				<tr>
					<td style="text-align: right;">密码：</td>
					<td><input id="password" name="password" type="password" class="login-input" placeholder="默认为：000000" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>
						<button type="submit">登录</button>
					</td>
				</tr>
			</table>
		</form>
		<hr />
		<div>
            <div style="float:left; width: 48%;margin-right: 2%;">
                <table border="1">
                    <caption>用户列表(密码：000000)</caption>
                    <tr>
                        <th width="50" style="text-align: center">用户名</th>
                        <th style="text-align: center">角色</th>
                    </tr>
                    <tr>
                        <td>admin</td>
                        <td>管理员、用户</td>
                    </tr>
                    <tr>
                        <td>kafeitu</td>
                        <td>用户</td>
                    </tr>
                    <tr>
                        <td>hruser</td>
                        <td>人事、用户</td>
                    </tr>
                    <tr>
                        <td>leaderuser</td>
                        <td>部门经理、用户</td>
                    </tr>
                </table>
            </div>

            <div style="float:right; width: 50%;">

                    <h5>组件版本信息</h5>
                    <ul>
                        <li>Activiti版本：${prop['activiti.version']}</li>
                        <li>Spring版本：${prop['spring.version']}</li>
                        <li>Database：${prop['db.type']}</li>
                        <li>使用<a href="http://maven.apache.org" target="_blank">Maven</a>管理依赖</li>
                    </ul>

            </div>


		</div>
        <hr />
        <div>

        </div>
        <hr />
    </div>
</body>
</html>
