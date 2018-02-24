package com.liangtee.jsuperlite.auditsys.AOP;

import com.liangtee.jsuperlite.auditsys.model.Menu;
import com.liangtee.jsuperlite.auditsys.model.Organization;
import com.liangtee.jsuperlite.auditsys.service.MenuService;
import com.liangtee.jsuperlite.auditsys.service.OrganizationService;
import com.liangtee.jsuperlite.auditsys.utils.TimeFormater;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/6/2.
 * All rights Reserved
 */

@Aspect
@Component
public class ControllerAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private MenuService menuService;

    @Pointcut("execution(* com.liangtee.jsuperlite.auditsys.web.internal.sys.UserController.show(..)) || " +
            "execution(* com.liangtee.jsuperlite.auditsys.web.internal.sys.UserController.getUserJsonList(..)) || " +
            "execution(* com.liangtee.jsuperlite.auditsys.web.internal.sys.OrganizationController.getOrgJsonList(..))")
    public void loadOrganizationInfo() {}

    @Pointcut("execution(* com.liangtee.jsuperlite.auditsys.web.internal.*.*.*(..))")
    public void loadMenuList() {}

    @Around("loadOrganizationInfo()")
    public String getOrganizationInfo(ProceedingJoinPoint pjp) throws Throwable {
        List<Organization> orgList = organizationService.getAll();
        request.setAttribute("orgList", orgList);

        return pjp.proceed().toString();
    }

    @Around("loadMenuList()")
    public String geMenuList(ProceedingJoinPoint pjp) throws Throwable {

        List<Menu> menuList = menuService.findAll("1 = ?", 1);
        request.setAttribute("menuList", menuList);

        return pjp.proceed().toString();
    }

}
