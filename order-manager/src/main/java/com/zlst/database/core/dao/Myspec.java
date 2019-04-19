//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zlst.database.core.dao;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zlst.database.common.Common;
import com.zlst.database.common.FieldUtil;
import com.zlst.param.Filters;
import com.zlst.param.Rule;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate.BooleanOperator;
import org.springframework.data.jpa.domain.Specification;

public class Myspec<T> implements Specification<T> {
    private Filters filters;

    public Myspec(Filters filters) {
        this.filters = filters;
    }

    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if(this.filters != null) {
            Predicate predicate = null;
            List<Predicate> predicates = new ArrayList();
            List<Rule> rules = this.filters.getRules();
            String op = this.filters.getGroupOp();
            List<Filters> filtersList = this.filters.getGroups();
            if(rules != null) {
                predicates.add(this.analyRules(root, cb, rules, op));
            }

            if(filtersList != null) {
                this.opGroupParam(root, cb, (Predicate)predicate, filtersList, op, predicates);
            }

            if(predicates != null && predicates.size() > 0) {
                query.where(this.analyWherePredicate(predicates, cb));
            }
        }

        return query.getRestriction();
    }

    private Predicate analyWherePredicate(List<Predicate> predicates, CriteriaBuilder cb) {
        Predicate predicateV = null;
        if(predicates != null && predicates.size() > 0) {
            for(int i = predicates.size() - 1; i >= 0; --i) {
                Predicate predicate = (Predicate)predicates.get(i);
                if(i == predicates.size() - 1) {
                    predicateV = cb.and(new Predicate[]{predicate});
                } else if(((Predicate)predicates.get(i + 1)).getOperator().equals(BooleanOperator.OR)) {
                    predicateV = cb.or(predicate, predicateV);
                } else {
                    predicateV = cb.and(predicate, predicateV);
                }
            }
        }

        return predicateV;
    }

    private Predicate opGroupParam(Root<T> root, CriteriaBuilder cb, Predicate predicate, List<Filters> filtersList, String parentOp, List<Predicate> predicates) {
        Predicate predicateReturn = null;
        if(filtersList != null && filtersList.size() > 0) {
            Iterator var8 = filtersList.iterator();

            while(var8.hasNext()) {
                Filters filters = (Filters)var8.next();
                if(Common.notNull(filters.getGroupOp()).booleanValue()) {
                    String op = filters.getGroupOp();
                    Predicate predicateNext;
                    if(op.equalsIgnoreCase("and")) {
                        if(filters.getRules() != null) {
                            predicateNext = this.analyRules(root, cb, filters.getRules(), op);
                            if(predicateNext != null) {
                                dealJoin(cb, parentOp, predicates, predicateNext);
                            }
                        }

                        this.opGroupParam(root, cb, (Predicate)predicateReturn, filters.getGroups(), filters.getGroupOp(), predicates);
                    } else if(op.equalsIgnoreCase("or")) {
                        predicateNext = this.analyRules(root, cb, filters.getRules(), op);
                        if(predicateNext != null) {
                            dealJoin(cb, parentOp, predicates, predicateNext);
                        }

                        this.opGroupParam(root, cb, (Predicate)predicateReturn, filters.getGroups(), filters.getGroupOp(), predicates);
                    }
                }
            }

            return (Predicate)predicateReturn;
        } else {
            return null;
        }
    }

    private void dealJoin(CriteriaBuilder cb, String parentOp, List<Predicate> predicates, Predicate predicateNext) {
        if(parentOp.equalsIgnoreCase("and")){
            predicates.add(cb.and(new Predicate[]{predicateNext}));
        }else if(parentOp.equalsIgnoreCase("or")) {
            predicates.add(cb.or(new Predicate[]{predicateNext}));
        }
    }

    private Predicate analyRules(Root<T> root, CriteriaBuilder cb, List<Rule> rules, String op) {
        if(rules != null && rules.size() > 0) {
            List<Predicate> predicateList = new ArrayList();
            Iterator var6 = rules.iterator();

            while(var6.hasNext()) {
                Rule rule = (Rule)var6.next();
                predicateList.add(this.opToJpa(rule, root, cb));
            }

            if(Common.notNull(op).booleanValue()) {
                if(op.equalsIgnoreCase("or")) {
                    return cb.or((Predicate[])predicateList.toArray(new Predicate[predicateList.size()]));
                }

                return cb.and((Predicate[])predicateList.toArray(new Predicate[predicateList.size()]));
            }
        }

        return null;
    }

    public Predicate opToJpa(Rule rule, Root<T> root, CriteriaBuilder cb) {
        String op = rule.getOp();
        String field = rule.getField();
        String data = rule.getData();
        Path leftHandSide = root.get(field);
        if(op != null) {
            byte var9 = -1;
            switch(op.hashCode()) {
                case 3148:
                    if(op.equals("bn")) {
                        var9 = 7;
                    }
                    break;
                case 3157:
                    if(op.equals("bw")) {
                        var9 = 6;
                    }
                    break;
                case 3179:
                    if(op.equals("cn")) {
                        var9 = 12;
                    }
                    break;
                case 3241:
                    if(op.equals("en")) {
                        var9 = 11;
                    }
                    break;
                case 3244:
                    if(op.equals("eq")) {
                        var9 = 0;
                    }
                    break;
                case 3250:
                    if(op.equals("ew")) {
                        var9 = 10;
                    }
                    break;
                case 3294:
                    if(op.equals("ge")) {
                        var9 = 5;
                    }
                    break;
                case 3309:
                    if(op.equals("gt")) {
                        var9 = 4;
                    }
                    break;
                case 3365:
                    if(op.equals("in")) {
                        var9 = 8;
                    }
                    break;
                case 3449:
                    if(op.equals("le")) {
                        var9 = 3;
                    }
                    break;
                case 3464:
                    if(op.equals("lt")) {
                        var9 = 2;
                    }
                    break;
                case 3509:
                    if(op.equals("nc")) {
                        var9 = 13;
                    }
                    break;
                case 3515:
                    if(op.equals("ni")) {
                        var9 = 9;
                    }
                    break;
                case 3523:
                    if(op.equals("nq")) {
                        var9 = 1;
                    }
            }

            switch(var9) {
                case 0:
                    return cb.equal(leftHandSide, this.convertToDateTypeIfNecessary(root, field, data));
                case 1:
                    return cb.notEqual(leftHandSide, this.convertToDateTypeIfNecessary(root, field, data));
                case 2:
                    return cb.lessThan(leftHandSide, this.convertToDateTypeIfNecessary(root, field, data));
                case 3:
                    return cb.lessThanOrEqualTo(leftHandSide, this.convertToDateTypeIfNecessary(root, field, data));
                case 4:
                    return cb.greaterThan(leftHandSide, this.convertToDateTypeIfNecessary(root, field, data));
                case 5:
                    return cb.greaterThanOrEqualTo(leftHandSide, this.convertToDateTypeIfNecessary(root, field, data));
                case 6:
                    return cb.greaterThanOrEqualTo(leftHandSide, this.convertToDateTypeIfNecessary(root, field, data));
                case 7:
                    return cb.lessThan(leftHandSide, this.convertToDateTypeIfNecessary(root, field, data));
                case 8:
                    if(data != null) {
                        return cb.and(new Predicate[]{leftHandSide.in(this.convertToDateArrayIfNecessary(root, field, data))});
                    }

                    return cb.equal(leftHandSide, this.convertToDateTypeIfNecessary(root, field, data));
                case 9:
                    if(data != null) {
                        return cb.not(leftHandSide.in(this.convertToDateArrayIfNecessary(root, field, data)));
                    }

                    return cb.notEqual(leftHandSide, this.convertToDateTypeIfNecessary(root, field, data));
                case 10:
                    return cb.lessThanOrEqualTo(leftHandSide, this.convertToDateTypeIfNecessary(root, field, data));
                case 11:
                    return cb.greaterThan(leftHandSide, this.convertToDateTypeIfNecessary(root, field, data));
                case 12:
                    return cb.like(leftHandSide, "%" + data + "%");
                case 13:
                    return cb.notLike(leftHandSide, "%" + data + "%");
                default:
                    return cb.equal(leftHandSide, this.convertToDateTypeIfNecessary(root, field, data));
            }
        } else {
            return null;
        }
    }

    private Comparable convertToDateTypeIfNecessary(Root<T> root, String field, String data) {
        return (Comparable)(Date.class.isAssignableFrom(root.get(field).getJavaType())?this.convertToDateType(root, field, data):data);
    }

    private Comparable[] convertToDateArrayIfNecessary(Root<T> root, String field, String data) {
        String[] elements = data.split(",");
        Comparable[] rightHandSideValue = new Comparable[elements.length];
        if(Date.class.isAssignableFrom(root.get(field).getJavaType())) {
            for(int i = 0; i < elements.length; ++i) {
                ((Object[])rightHandSideValue)[i] = this.convertToDateType(root, field, elements[i]);
            }
        } else {
            rightHandSideValue = elements;
        }

        return (Comparable[])rightHandSideValue;
    }

    private Comparable convertToDateType(Root<T> root, String field, String data) {
        String datePattern = this.getDatePattern(root, field);
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);

        Object rightHandSideValue;
        try {
            rightHandSideValue = sdf.parse(data);
        } catch (ParseException var8) {
            rightHandSideValue = data;
        }

        return (Comparable)rightHandSideValue;
    }

    private String getDatePattern(Root<T> root, String field) {
        Class<? extends T> entityClass = root.getJavaType();
        Field entityField = FieldUtil.getDeclaredField(entityClass, field);
        JsonFormat jsonFormatAnnotation = (JsonFormat)entityField.getAnnotation(JsonFormat.class);
        return jsonFormatAnnotation == null?"yyyy-MM-dd HH:mm:ss":jsonFormatAnnotation.pattern();
    }
}
