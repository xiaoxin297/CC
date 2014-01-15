package com.smarcloud.core.ldap.dao;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.directory.SearchControls;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.simple.ParameterizedContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.WhitespaceWildcardsFilter;
import org.springframework.stereotype.Repository;

import com.smarcloud.core.annotation.ldap.Attribute;
import com.smarcloud.core.annotation.ldap.Dn;
import com.smarcloud.core.annotation.ldap.Entry;
import com.smarcloud.core.annotation.ldap.Id;
import com.smarcloud.core.utils.Page;

/**
 * 
 * 对LDAP DAO的支持
 * 
 * @author robin wang
 * 
 * @param <T>
 */
@Repository
@SuppressWarnings("unchecked")
public class LDAPDaoSuport<T> {

	/**
	 * 当前泛型的class
	 * */
	protected Class<?> entityClass = null;

	protected Map<String, Object> attributeMap = null;

	protected Map<String, String> fieldMap = new HashMap<>();

	@Autowired
	private LdapTemplate ldapTemplate;

	public LDAPDaoSuport() {
		entityClass = (Class<?>) getTClass();
	}

	/**
	 * 对LDAP操作行为
	 * 
	 * @author robin wang
	 * 
	 */
	private enum Action {
		CREATE, UPDATE
	}

	/**
	 * Filter查询类型(模糊 or 精确)
	 * 
	 * @author robin wang
	 */
	private enum SearchType {
		WHITESPACE, EQUERY
	}

	/**
	 * LDAP 实体字段
	 * 
	 * @author robin wang
	 * 
	 */
	private interface LDAPField {
		String ID = "id";
		String DN = "dn";
		String objectClass = "objectclass";
	}

	private Class<?> getTClass() {
		Type genType = this.getClass().getGenericSuperclass();
		if (!(genType instanceof ParameterizedType)) {
			return Object.class;
		}
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		return (Class<?>) params[0];
	}

	public LdapTemplate getLdapTemplate(T t) {
		parseAnnotation(t);
		return ldapTemplate;
	}

	/**
	 * 创建新条目
	 * 
	 * @param t
	 * @throws Exception
	 */
	protected void create(T t) throws Exception {
		getLdapTemplate(t).bind(getDirContextOperation(Action.CREATE));
	}

	/**
	 * 更新条目
	 * 
	 * @param t
	 * @throws Exception
	 */
	public void update(T t) throws Exception {
		getLdapTemplate(t).modifyAttributes(getDirContextOperation(Action.UPDATE));
	}

	/**
	 * 删除条目
	 * 
	 * @param t
	 * @throws Exception
	 */
	protected void delete(T t) throws Exception {
		getLdapTemplate(t).unbind(buildDn());
	}


	/**
	 * 匹配objectclass的所有条目
	 * 
	 * @param t
	 * @return
	 */
	protected List<T> findAll(T t) {
		parseAnnotation(t);
		return search(t, getObjectclassFilter());
	}

	/**
	 * 模糊查询列表
	 * @param t
	 * @return
	 */
	protected List<T> findListByWhiteFilter(T t) {
		return search(t, getFilter(t, SearchType.WHITESPACE));
	}
	
	/**
	 * 精确查询列表
	 * @param t
	 * @return
	 */
	protected List<T> findListByEqFilter(T t) {
		return search(t, getFilter(t, SearchType.EQUERY));
	}
	
	/**
	 * 根据dn直接获取实体信息
	 * 
	 * @param t
	 * @param dn
	 * @return
	 * @创建人 PengBo
	 * @创建时间 2013-7-29 下午4:39:59
	 */
	protected T findByID(T t) {
		return (T) getLdapTemplate(t).lookup(buildDn(), getContextMapper(t));
	}
	
	/**
	 * 通过filter查询ldap条目列表
	 * 
	 * @param t
	 * @param filter
	 * @return
	 */
	private List<T> search(T t, AndFilter filter) {
		return ldapTemplate.search(buildDn(), filter.encode(), getContextMapper(t));
	}
	
	/**
	 * 查分页信息
	 * 
	 * @param basePage
	 * @return
	 */
	public Page<T> getPages(Page<T> basePage, T t) {
		int totalRow = findListByWhiteFilter(t).size();
		basePage.setContent(getAllPageMap(null, t, (basePage.getPageSize() * basePage.getPage())));
		basePage.setTotalRow(totalRow);
		basePage.setTotalPage((totalRow + basePage.getPageSize() - 1) / basePage.getPageSize());
		return basePage;
	}

	/**
	 * 查询分页结果集
	 * 
	 * @param cookie
	 * @param t
	 * @param pageSize
	 * @return
	 */
	private List<T> getAllPageMap(PagedResultsCookie cookie, T t, Integer pageSize) {
		PagedResultsDirContextProcessor control = new PagedResultsDirContextProcessor( pageSize, cookie);
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		List<T> mList = ldapTemplate.search(buildDn(), getFilter(t,SearchType.WHITESPACE).encode(), searchControls, getContextMapper(t), control);
		return mList;
	}
	
	
	

	private AndFilter getFilter(T t, SearchType type) {
		parseAnnotation(t);
		AndFilter filter = new AndFilter();
		String fv = null;
		for (String o : attributeMap.keySet()) {
			if (o.equals(LDAPField.objectClass)) {
				filter.and(getObjectclassFilter());
				continue;
			}
			Object v = attributeMap.get(o);
			if(o.equals(LDAPField.ID) || o.equals(LDAPField.DN) || v == null){
				continue;
			}else{
				fv = (String) v;
			}
			if (type.equals(SearchType.EQUERY)) {
				filter.and(new EqualsFilter(o, fv));
			} else if (type.equals(SearchType.WHITESPACE)) {
				filter.and(new WhitespaceWildcardsFilter(o, fv));
			}
		}
		return filter;
	}

	private AndFilter getObjectclassFilter() {
		AndFilter filter = new AndFilter();
		String  obs = (String) attributeMap.get(LDAPField.objectClass);
		filter.and(new EqualsFilter(LDAPField.objectClass, obs));
		return filter;
	}

	/**
	 * 获取DirContextOperations
	 * 
	 * @param t
	 * @return
	 */
	private DirContextOperations getDirContextOperation(Action action) {
		Name name = buildDn();
		DirContextOperations context = null;
		if (action.equals(Action.CREATE)) {
			context = new DirContextAdapter(name);
		} else if (action.equals(Action.UPDATE)) {
			context = ldapTemplate.lookupContext(name);
		}
		for (String key : attributeMap.keySet()) {
			if (!key.equals(LDAPField.DN) && !key.equals(LDAPField.ID) && attributeMap.get(key) != null) {
				context.setAttributeValue(key, attributeMap.get(key));
			}
		}
		return context;
	}

	/**
	 * 获取ContextMapper
	 * 
	 * @return ContextMapper
	 */
	private ContextMapper getContextMapper(final T t) {
		return new ParameterizedContextMapper<T>() {
			@Override
			public T mapFromContext(Object ctx) {
				T nt = null;
				DirContextAdapter adapter = (DirContextAdapter) ctx;
				Field[] fields = entityClass.getDeclaredFields();
				try {
					 nt = (T)entityClass.newInstance();
				} catch (InstantiationException | IllegalAccessException e1) {
					e1.printStackTrace();
				} 
				try {
					Attribute attribute = null;
					Id id = null;
					for(Field field : fields){
						field.setAccessible(true);
						attribute = field.getAnnotation(Attribute.class);
						id = field.getAnnotation(Id.class);
						if (id != null) {
							Name idName = adapter.getDn();
							field.set(nt, new DistinguishedName(idName.getPrefix(idName.size()-1)));
							continue;
						}
						if (attribute != null) {
							String name = attribute.name();
							for(String key : attributeMap.keySet()){
								//如果是特殊属性就跳过
								if ("objectclass".equals(key) || "userPassword".equals(key)) {
									continue;
								}
								//如果ldap中的属性没有值跳过
								String ldapVal = adapter.getStringAttribute(key);
								if (StringUtils.isBlank(ldapVal)) {
									continue;
								}
								//如果属性名称不为空并且属性名称和map里面的对应就设置此字段的值为ldap中查询到的值
								if (StringUtils.isNotBlank(name) && key.equals(name)) {
									field.set(nt, ldapVal);
									break;
								}
							}
						}
					}
				} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
				return nt;
			}
		};
	}

	/**
	 * 解析注解
	 * 
	 * @param t
	 * @return
	 */
	private void parseAnnotation(T t) {
		attributeMap = new HashMap<>();
		Field[] fields = entityClass.getDeclaredFields();
		Attribute attribute = null;
		Dn dn = null;
		Id id = null;
		Entry entry = null;
		try {
			entry = entityClass.getAnnotation(Entry.class);
			if (entry != null) {
				String[] objectClass = entry.objectClass();
				if (objectClass != null) {
					for (String ob : objectClass) {
						attributeMap.put(LDAPField.objectClass, ob);
					}
				}
			}
			for (Field field : fields) {
				field.setAccessible(true);
				attribute = field.getAnnotation(Attribute.class);
				dn = field.getAnnotation(Dn.class);
				id = field.getAnnotation(Id.class);
				Object fieldVal = field.get(t);
				if (attribute != null) {
					String fieldKey = attribute.name();
					if (fieldKey == null) {
						fieldKey = field.getName();
					}
					attributeMap.put(fieldKey, fieldVal);
				}
				if (dn != null) {
					if(fieldVal==null){
						attributeMap.put(LDAPField.DN, null);
					}else{
						attributeMap.put(LDAPField.DN, field.getName() + "=" + fieldVal);
					}
				}
				if (id != null) {
					attributeMap.put(LDAPField.ID, fieldVal);
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置DN
	 * 
	 * @return
	 */
	private Name buildDn() {
		DistinguishedName name = new DistinguishedName();
		name = (DistinguishedName) attributeMap.get(LDAPField.ID);
		String dn = (String) attributeMap.get(LDAPField.DN);
		if (dn != null) {
			try {
				name.add(dn);
			} catch (InvalidNameException e) {
				e.printStackTrace();
			}
		}
		return name;
	}
}
