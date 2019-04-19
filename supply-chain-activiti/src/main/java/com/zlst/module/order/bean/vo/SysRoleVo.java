package com.zlst.module.order.bean.vo;

public class SysRoleVo {

    private String roleId;  //角色ID
    private String roleName;  //角色名称
    private String remark;  //角色描述

    public SysRoleVo(){}

    public SysRoleVo(String roleId,String roleName,String remark){
       this.roleId = roleId;
       this.roleName = roleName;
       this.remark = remark;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
