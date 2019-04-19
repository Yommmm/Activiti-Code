package com.zlst.module.demo.bean;

import com.zlst.database.core.bean.AbstractEntityNoIDBean;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;

/**
 * Created by user03 on 2017/7/10.
 */
@Table(name = "demo_jpa99")
@Entity
@DynamicUpdate
@Indexed
public class DemoBean extends AbstractEntityNoIDBean {

    /**
     * ID 32位，系统自动生成
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
    private String demoId;

    @Column(name = "first_name")
    @Field
    private String firstName;
    @Column(name = "last_name")
    @Field
    private String lastName;
    @Column(nullable = false)
    private Integer age;
    @Column(nullable = false)
    private String gender;
    @Column(nullable = false)
    private String sno;

    public String getDemoId() {
        return demoId;
    }

    public void setDemoId(String demoId) {
        this.demoId = demoId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }
}
