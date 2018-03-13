package com.liangtee.jsuperlite.core.AOP;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.liangtee.jsuperlite.core.Annotation.AccessControl;
import com.liangtee.jsuperlite.core.Annotation.AccessControlJson;
import com.liangtee.jsuperlite.core.Annotation.PermissionValidate;
import com.liangtee.jsuperlite.core.model.Menu;
import com.liangtee.jsuperlite.core.model.Role;
import com.liangtee.jsuperlite.core.model.User;
import com.liangtee.jsuperlite.core.service.MenuService;
import com.liangtee.jsuperlite.core.service.RoleService;
import com.liangtee.jsuperlite.core.utils.TimeFormater;
import com.liangtee.jsuperlite.core.values.json.ReturnMessage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@Aspect
@Component
public class PermissionAspect {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private RoleService roleService;

	@Autowired
	private MenuService menuService;

	@Pointcut("execution(* com.liangtee.jsuperlite.core.web.internal.sys.*.*(..))")
	public void isLogin() {}

	@Deprecated
	@Pointcut("@annotation(com.liangtee.jsuperlite.core.Annotation.AccessControl)")
	public void accessControl() {}

	@Deprecated
	@Pointcut("@annotation(com.liangtee.jsuperlite.core.Annotation.AccessControlJson)")
	public void accessControlJson() {}

	@Pointcut("@annotation(com.liangtee.jsuperlite.core.Annotation.PermissionValidate)")
	public void permissionValidate() {}

	@Around("isLogin()")
	public String loginCheck(ProceedingJoinPoint pjp) throws Throwable {
		if(request.getSession().getAttribute("user") == null) return "redirect:/";
		request.setAttribute("currentDate", TimeFormater.format("yyyy/MM/dd"));
		return pjp.proceed().toString();
	}

	@Around(value = "permissionValidate()&&@annotation(permissionValidate)")
	public String permissionValidation(ProceedingJoinPoint pjp, PermissionValidate permissionValidate) throws Throwable {

		User user = (User) request.getSession().getAttribute("user");

		Role role = roleService.findOne(user.getRoleID());
		System.out.println("###"+user.getRoleID());
		if(role.getGrantedMenuIDs() == null || role.getGrantedMenuIDs().isEmpty()) return "content_pages/denied";

		if(!role.getGrantedMenuIDs().equalsIgnoreCase(Role.GRANTED_ALL)) {
			Set<Integer> grantedIDs = new HashSet<Integer>();
			Arrays.stream(role.getGrantedMenuIDs().split(",")).forEach(id -> grantedIDs.add(Integer.valueOf(id)));
			Menu targetMenu = menuService.findAll("url = ?", request.getRequestURI()).get(0);
			if(!grantedIDs.contains(targetMenu.getId())) return "content_pages/denied";
		}

		return pjp.proceed().toString();
	}

}
	 
