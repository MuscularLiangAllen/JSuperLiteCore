package com.liangtee.jsuperlite.core.web.internal.sys;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.liangtee.jsuperlite.core.Annotation.PermissionValidate;
import com.liangtee.jsuperlite.core.model.Role;
import com.liangtee.jsuperlite.core.service.RoleService;
import com.liangtee.jsuperlite.core.service.base.PageModel;
import com.liangtee.jsuperlite.core.utils.InjectionFilter;
import com.liangtee.jsuperlite.core.values.json.ReturnMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Allen on 2018/2/2.
 */


@Controller
@RequestMapping("/sys/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PermissionValidate
    @RequestMapping(path = "show", method = RequestMethod.GET)
    public String show(@RequestParam(name="pageNumber", required = false) Integer pageNumber, HttpServletRequest request, Model model) {

        List<Role> roleList = roleService.findByPage(new PageModel(pageNumber == null ? 1 : pageNumber), null, -1, "1 = ?", "1");
        model.addAttribute("roleList", roleList);

        return "content_pages/sys-roles";
    }

    @PermissionValidate
    @RequestMapping(path = "add", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String add(@RequestParam(value = "roleName", required = true) String roleName,
                                    @RequestParam(value = "roleDesc", required = false) String roleDesc,
                                    @RequestParam(value = "grantedIDs", required = false) String grantedIDs,
                                    @RequestParam(value = "grantedNames", required = false) String grantedNames,
                                    @RequestParam(value = "isEditable", required = true) boolean isEditable) {


        if(InjectionFilter.filter(roleName, roleDesc, grantedNames))
            return JSON.toJSONString(new ReturnMessage("(警告)拒绝访问"));

        Role role = new Role(roleName);
        role.setDescription(roleDesc);
        role.setGrantedMenuIDs(grantedIDs);
        role.setGrantedMenuNames(grantedNames);
        role.setEditable(isEditable);

        return roleService.add(role) != null ? JSON.toJSONString(new ReturnMessage("创建角色成功")):
                JSON.toJSONString(new ReturnMessage("创建角色失败"));

    }

    @PermissionValidate
    @RequestMapping(path = "role-list-json", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody String getRoleJsonList(HttpServletRequest request) {

        List<Role> roleList = roleService.findAll("1 = ?", 1);
        System.out.println(JSON.toJSONString(roleList, SerializerFeature.DisableCircularReferenceDetect));
        return JSON.toJSONString(roleList, SerializerFeature.DisableCircularReferenceDetect);
    }

}
