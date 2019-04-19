package com.zlst.module.demo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zlst.database.core.bean.AbstractEntityBean;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by user03 on 2017/8/2.
 */
@Entity
@DynamicUpdate
@Indexed
public class Author extends AbstractEntityBean  implements Serializable {

    @Field
    private String name;

    @ManyToMany(mappedBy = "authors")
    @JsonIgnore
    private Set<Book> books;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }
}
