package com.zlst.module.order.service;

import com.zlst.param.Page;
import com.zlst.param.PageParam;
import org.hibernate.MappingException;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.criteria.ValueHandlerFactory;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 170028 on 2017/9/8.
 */
@Service
public class MyNvativeSqlQueryServ {
    @PersistenceContext
    EntityManager entityManager;

    /**
     * 分页查询，返回objList元素的类型为Object[]
     *
     * @param nativeSql 原生sql
     * @param pageParam 分页参数
     * @return
     */
    public Page nativeSqlPageQuery(String nativeSql, PageParam pageParam) {
        return nativeSqlPageQueryProcess(nativeSql, pageParam, null,null);
    }

    /**
     * 分页查询，返回objList元素的类型为Object[]
     *
     * @param nativeSql 原生sql
     * @param pageParam 分页参数
     * @param params    Map类型的查询参数
     * @return
     */
    public Page nativeSqlPageQuery(String nativeSql, PageParam pageParam, Map<String, Object> params) {
        return nativeSqlPageQueryProcess(nativeSql, pageParam, null, params);
    }

    /**
     * 分页查询，返回objList元素的类型为resultClass，如果resultClass为null,则为Object[]
     *
     * @param nativeSql   原生sql
     * @param pageParam   分页参数
     * @param resultClass 结果集元素类型 可以是Map,List或者自定义vo类型，如果是自定义vo类型，resultClass中的字段必须和查询的列一致
     * @return
     */
    public <T> Page<T> nativeSqlPageQuery(String nativeSql, PageParam pageParam, Class<T> resultClass) {
        return nativeSqlPageQueryProcess(nativeSql, pageParam, resultClass,  null);
    }

    /**
     * 分页查询，返回objList元素的类型为resultClass，如果resultClass为null,则为Object[]
     *
     * @param nativeSql   原生sql
     * @param pageParam   分页参数
     * @param resultClass 结果集元素类型 可以是Map,List或者自定义vo类型，如果是自定义vo类型，resultClass中的字段必须和查询的列一致
     * @param params      Map类型的查询参数
     * @return
     */
    public <T> Page<T> nativeSqlPageQuery(String nativeSql, PageParam pageParam, Class<T> resultClass, Map<String, Object> params) {
        return nativeSqlPageQueryProcess(nativeSql, pageParam, resultClass, params);
    }

    /**
     * 分页查询，返回objList元素的类型为resultClass，如果resultClass为null,则为Object[]
     * @param nativeSql   原生sql
     * @param pageParam   分页参数
     * @param resultClass 结果集元素类型 可以是Map,List或者自定义vo类型，如果是自定义vo类型，resultClass中的字段必须和查询的列一致
     * @param params      可变长度的查询参数
     * @return
     */
    public <T> Page<T> nativeSqlPageQuery(String nativeSql, PageParam pageParam, Class<T> resultClass, Object... params) {
        if(params.length==0){
            return nativeSqlPageQueryProcess(nativeSql, pageParam, resultClass, null);
        }
        return nativeSqlPageQueryProcess(nativeSql, pageParam, resultClass, params);
    }

    /**
     * 普通查询，返回objList元素的类型为resultClass，如果resultClass为null,则为Object[]
     *
     * @param nativeSql   原生sql
     * @param resultClass 结果集元素类型 可以是Map,List或者自定义vo类型，如果是自定义vo类型，resultClass中的字段必须和查询的列一致
     * @param params      查询参数，map或者object[]、object
     * @return
     */
    public <T> List<T> nativeSqlQuery(String nativeSql, Class<T> resultClass, Object params) {
        Query query = createSqlQuery(nativeSql, resultClass);
        if (params != null) {
            if (params instanceof Map) {
                for (Map.Entry<String, Object> entry : ((Map<String, Object>) params).entrySet()) {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            } else if (params.getClass().isArray()) {
                for (int i = 1; i <= Array.getLength(params); i++) {
                    query.setParameter(i, Array.get(params, i - 1));
                }
            } else {
                query.setParameter(1, params);
            }
        }
        return query.getResultList();
    }

    /**
     * 分页查询，返回objList元素的类型为resultClass，如果resultClass为null,则为Object[]
     *
     * @param nativeSql   原生sql
     * @param pageParam   分页参数
     * @param resultClass 结果集元素类型 可以是Map,List或者自定义vo类型，如果是自定义vo类型，resultClass中的字段必须和查询的列一致
     * @param params      查询参数，map或者object[]、object
     * @return
     */
    protected <T> Page<T> nativeSqlPageQueryProcess(String nativeSql, PageParam pageParam, Class<T> resultClass, Object params) {
        Query query = createSqlQuery(nativeSql, resultClass);

        String countSql;
        Pattern groupByPattern = Pattern.compile("group\\s+by", Pattern.CASE_INSENSITIVE);
        Matcher m = groupByPattern.matcher(nativeSql);
        //含有group by的统一在sql语句外面套一层select count(1) from ,不含的则使用将select语句替换成select count(1)
        if (m.find()) {
            StringBuilder sb = new StringBuilder();
            sb.append("select count(1) from ( ").append(nativeSql).append(" )");
            countSql = sb.toString();
        } else {
            Pattern pattern = Pattern.compile("select.+?from", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(nativeSql);
            countSql = matcher.replaceFirst("select count(1) from ");
        }

        //设置查询参数
        Query countQuery = entityManager.createNativeQuery(countSql);
        if (params != null) {
            if (params instanceof Map) {
                for (Map.Entry<String, Object> entry : ((Map<String, Object>) params).entrySet()) {
                    query.setParameter(entry.getKey(), entry.getValue());
                    countQuery.setParameter(entry.getKey(), entry.getValue());
                }
            } else if (params.getClass().isArray()) {
                for (int i = 1; i <= Array.getLength(params); i++) {
                    query.setParameter(i, Array.get(params, i - 1));
                    countQuery.setParameter(i, Array.get(params, i - 1));
                }
            } else {
                query.setParameter(1, params);
                countQuery.setParameter(1, params);
            }
        }

        //设置分页参数
        int page = pageParam.getPageNum();
        int pageSize = pageParam.getPageSize();
        int firstNum = pageSize * (page - 1);
        int lastNum = firstNum + pageSize;
        query.setFirstResult(firstNum);
        query.setMaxResults(lastNum);

        //组装分页结果
        Page pageObject = new Page();
        Long maxNum = ValueHandlerFactory.convert(countQuery.getSingleResult(),Long.class);
        Long maxPage = (maxNum % pageSize == 0) ? maxNum / pageSize : maxNum / pageSize + 1;
        pageObject.setCount(maxNum);
        pageObject.setMaxPage(maxPage.intValue());
        pageObject.setPageNum(page);
        pageObject.setPageSize(pageSize);
        pageObject.setObjList(query.getResultList());
        return pageObject;
    }

    private Query createSqlQuery(String nativeSql, Class resultClass) {
        Query query;
        if (resultClass == null) {
            query = entityManager.createNativeQuery(nativeSql);
        } else {
            SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);
            //如果resultClass是jpa管理的实体类类型
            try{
                sessionFactory.getClassMetadata(resultClass);
                query = entityManager.createNativeQuery(nativeSql, resultClass);
            }catch(MappingException e){
                //resultClass不是jpa管理的实体类类型
                query = entityManager.createNativeQuery(nativeSql);
                SQLQuery sqlQuery = query.unwrap(SQLQuery.class);
                if (resultClass == Map.class) {
                    sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
                } else if (resultClass == List.class) {
                    sqlQuery.setResultTransformer(Transformers.TO_LIST);
                } else {
                    sqlQuery.setResultTransformer(Transformers.aliasToBean(resultClass));
                    Field[] fields = resultClass.getDeclaredFields();
                    for (int i = 0; i < fields.length; i++) {
                        sqlQuery.addScalar(fields[i].getName());
                    }
                }
            }
//            if (sessionFactory.getClassMetadata(resultClass) != null) {
//                query = entityManager.createNativeQuery(nativeSql, resultClass);
//            } else {
//
//            }
        }
        return query;
    }

}
