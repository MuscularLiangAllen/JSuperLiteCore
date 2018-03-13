package com.liangtee.jsuperlite.core.web.internal.sys;

import com.liangtee.jsuperlite.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liangtee on 2017/6/16.
 */

@Component
public class BaseController {

    @Autowired
    protected HttpServletRequest request;

    protected static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    protected static final String PATH_SEPARATOR = System.getProperty("file.separator");

    protected static final String DEFAULT_TEMPLATE_FILE_PATH = "/downloads/template-files";

    public User getOperator() {
        return  (User) request.getSession().getAttribute("user");
    }

}
