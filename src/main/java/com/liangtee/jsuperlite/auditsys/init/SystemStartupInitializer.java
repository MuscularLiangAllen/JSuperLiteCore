package com.liangtee.jsuperlite.core.init;

import com.liangtee.jsuperlite.core.model.Menu;
import com.liangtee.jsuperlite.core.model.Organization;
import com.liangtee.jsuperlite.core.model.Role;
import com.liangtee.jsuperlite.core.model.User;
import com.liangtee.jsuperlite.core.service.MenuService;
import com.liangtee.jsuperlite.core.service.OrganizationService;
import com.liangtee.jsuperlite.core.service.RoleService;
import com.liangtee.jsuperlite.core.service.UserService;
import com.liangtee.jsuperlite.core.utils.FileUtils;
import com.liangtee.jsuperlite.core.utils.MD5Encoder;
import com.liangtee.jsuperlite.core.values.OrgConfs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/4/18.
 * All rights Reserved
 */

@Component
public class SystemStartupInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SystemStartupInitializer.class);

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private RoleService roleService;

    @Override
    public void run(String... strings) throws Exception {

        System.out.println("===================================================");
        System.out.println("          JSuperLite Core 快速开发平台              ");
        System.out.println("      Author: LIANG Tianyi (Allen LIANG)           ");
        System.out.println("            All Rights Reserved                    ");
        System.out.println("===================================================");


        if(!menuService.isExist("NAME = ?", "角色管理"))
            menuService.add(new Menu("角色管理", "/sys/roles/show", -1, "fa fa-user-circle"));
        if(!menuService.isExist("NAME = ?", "组织机构管理"))
            menuService.add(new Menu("组织机构管理", "/sys/org/show", -1, "fa fa-sitemap"));
        if(!menuService.isExist("NAME = ?", "用户管理"))
            menuService.add(new Menu("用户管理", "/sys/users/show", -1, "fa fa-users"));
        if(!menuService.isExist("NAME = ?", "菜单管理"))
            menuService.add(new Menu("菜单管理", "/sys/menus/show", -1, "fa fa-list-alt"));
        if(!menuService.isExist("NAME = ?", "文件管理"))
            menuService.add(new Menu("文件管理", "/sys/files/show", -1, "fa fa-files-o"));


        Organization org = null;
        if(!organizationService.isExist("ORG_NAME = ?", "运营维护中心"))
            org = organizationService.add(new Organization(OrgConfs.NO_PARENT_ORG, 1, "运营维护中心",
                    OrgConfs.TypeCode.ORG_TYPE_DEPT, "18660163087"));
        else org = organizationService.findAll("ORG_NAME = ?", "运营维护中心").get(0);


        Role superUser = null;
        if(!roleService.isExist("NAME = ?", "超级管理员")) {
            superUser = new Role("超级管理员");
            superUser.setGrantedMenuIDs(Role.GRANTED_ALL);
            superUser.setEditable(false);
            superUser = roleService.add(superUser);
        } else {
            superUser = roleService.findAll("NAME = ?", "超级管理员").get(0);
        }

        System.out.println("su: ###" + superUser.getId());

        userService.add("kingroot", "504B2BA8E6B0B74FE61AF448D6C3772D", superUser.getId(),
                org.getID(), "liangtee@126.com", "13000000000", true);

        log.info("###### The Super Admin User has been created ... ######");

//        System.setProperty("user.home", System.getProperty("user.home")+System.getProperty("file.separator")+"JSuperLiteFS");
        System.setProperty("sys.home", String.format("%s%sJSuperLiteFS_Core", System.getProperty("user.home"), System.getProperty("file.separator")));
        System.setProperty("sys.workplace", String.format("%s%sworkplace", System.getProperty("sys.home"), System.getProperty("file.separator")));
        System.setProperty("sys.tmp.upload", String.format("%s%stmp_upload", System.getProperty("sys.home"), System.getProperty("file.separator")));
        System.setProperty("sys.trash",String.format("%s%strash", System.getProperty("sys.home"), System.getProperty("file.separator")));

        FileUtils.mkdir(System.getProperty("sys.home"));
        FileUtils.mkdir(System.getProperty("sys.workplace"));
        FileUtils.mkdir(System.getProperty("sys.tmp.upload"));
        FileUtils.mkdir(System.getProperty("sys.trash"));

        log.info("###### System initialized successfully ... ######");

    }
}
