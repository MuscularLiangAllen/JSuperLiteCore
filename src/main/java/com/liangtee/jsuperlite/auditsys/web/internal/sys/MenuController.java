package com.liangtee.jsuperlite.auditsys.web.internal.sys;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.liangtee.jsuperlite.auditsys.Annotation.PermissionValidate;
import com.liangtee.jsuperlite.auditsys.model.Menu;
import com.liangtee.jsuperlite.auditsys.model.User;
import com.liangtee.jsuperlite.auditsys.service.MenuService;
import com.liangtee.jsuperlite.auditsys.service.base.PageModel;
import com.liangtee.jsuperlite.auditsys.utils.InjectionFilter;
import com.liangtee.jsuperlite.auditsys.values.json.ReturnMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Allen on 2018/1/31.
 */

@Controller
@RequestMapping("/sys/menus")
public class MenuController extends BaseController {

    @Autowired
    private MenuService menuService;

    @PermissionValidate
    @RequestMapping(path = "show", method = RequestMethod.GET)
    public String show(@RequestParam(name="pageNumber", required = false) Integer pageNumber, HttpServletRequest request, Model model) {

        User user = (User) request.getSession().getAttribute("user");
        List<Menu> menuList = menuService.findByPage(new PageModel(pageNumber == null ? 1 : pageNumber), null, -1, "1 = ?", "1");

        model.addAttribute("menuList", menuList);


        return "content_pages/sys-menus";
    }

    @PermissionValidate
    @RequestMapping(path = "add", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String add(@RequestParam(value = "menuName", required = true) String menuName,
               @RequestParam(value = "menuDesc", required = true) String menuDesc,
               @RequestParam(value = "menuUrl", required = true) String menuUrl,
               @RequestParam(value = "params", required = true) String params,
               @RequestParam(value = "menuIcon", required = true) String menuIcon,
               @RequestParam(value = "menuWeight", required = true) String menuWeight) {


        if(InjectionFilter.filter(menuName, menuDesc, menuUrl, params, menuIcon, menuWeight))
            return JSON.toJSONString(new ReturnMessage("(警告)拒绝访问"));

        Menu menu = new Menu(menuName, menuUrl, -1, menuIcon);
        menu.setDescription(menuDesc);
        menu.setParams(params);

        return menuService.add(menu) != null ? JSON.toJSONString(new ReturnMessage("创建菜单成功")):
                JSON.toJSONString(new ReturnMessage("创建菜单失败"));

    }

    @PermissionValidate
    @RequestMapping(path = "delete", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String delete(@RequestParam(value = "menuID", required = true) int menuID) {

        try {
            menuService.delete(menuID);
            return JSON.toJSONString(new ReturnMessage("删除菜单成功"));
        } catch (Exception e) {
            return JSON.toJSONString(new ReturnMessage("删除菜单失败"));
        }
    }

    @PermissionValidate
    @RequestMapping(path = "menu-list-json", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody String getMenuJsonList(HttpServletRequest request) {

        List<Node> zTreeNodeList = new ArrayList<Node>();
        menuService.getAll().forEach(m -> zTreeNodeList.add(new Node(m.getId(), m.getBelongTo(), m.getName())));

        return JSON.toJSONString(zTreeNodeList, SerializerFeature.DisableCircularReferenceDetect);
    }


    class Node {
        @JSONField(name="id")
        int ID;
        @JSONField(name="pId")
        int pID;
        @JSONField(name="name")
        String name;

        public Node() {}

        public Node(int ID, int pID, String name) {
            this.ID = ID;
            this.pID = pID;
            this.name = name;
        }

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public int getpID() {
            return pID;
        }

        public void setpID(int pID) {
            this.pID = pID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
