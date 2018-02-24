package com.liangtee.jsuperlite.auditsys.web.internal.sys;

import com.liangtee.jsuperlite.auditsys.Annotation.AccessControl;
import com.liangtee.jsuperlite.auditsys.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/4/21.
 * All rights Reserved
 */

@Controller
@RequestMapping("/sys/dashboard")
public class DashboardController {

    @RequestMapping(path = "show", method = RequestMethod.GET)
    public String show(HttpServletRequest request, Model model) {

        User user = (User) request.getSession().getAttribute("user");

        return "modules/framework";
    }

    @RequestMapping(path = "content", method = RequestMethod.GET)
    public String getDashBoard(HttpServletRequest request, Model model) {

        User user = (User) request.getSession().getAttribute("user");

        return "content_pages/dashboard";
    }

}
