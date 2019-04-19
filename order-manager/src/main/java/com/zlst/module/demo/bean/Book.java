package com.zlst.module.demo.bean;


import com.zlst.database.core.bean.AbstractEntityBean;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by user03 on 2017/8/2.
 */
@Entity
@DynamicUpdate
@Indexed
public class Book  extends AbstractEntityBean implements Serializable {

    @Field
    private String bookName;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "BOOK_AUTHOR", joinColumns = {
            @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID")}, inverseJoinColumns = {
            @JoinColumn(name = "AUTHOR_ID", referencedColumnName = "ID")})
    private Set<Author> authors;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }
}
