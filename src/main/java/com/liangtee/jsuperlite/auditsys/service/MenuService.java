package com.liangtee.jsuperlite.core.service;

import com.liangtee.jsuperlite.core.model.Menu;
import com.liangtee.jsuperlite.core.repository.MenuRepository;
import com.liangtee.jsuperlite.core.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Allen on 2018/1/31.
 */

@Component("menuService")
public class MenuService extends BaseService<Menu, Integer> {

    private MenuRepository menuRepository;

    @Autowired
    public MenuService(JdbcTemplate jdbcTemplate, MenuRepository menuRepository) {
        super(jdbcTemplate);
        this.menuRepository = menuRepository;
    }

    public Menu add(Menu menu) {return menuRepository.save(menu);}

    public void delete(int menuID) {menuRepository.delete(menuID);}

    public List<Menu> getAll() {

        List<Menu> result = new LinkedList<Menu>();

        List<Menu> rootNodes = findAll("belong_to = ?", Menu.ROOM_MENU);
        Queue<Menu> queue = new LinkedList<Menu>();
        queue.addAll(rootNodes);

        Set<Menu> IDs = new HashSet<Menu>();

        while (!queue.isEmpty()) {
            Menu menu = queue.poll();
            if(!IDs.contains(menu)) {
                result.add(menu);
                IDs.add(menu);
            }
            List<Menu> childNodes = findAll("belong_to = ?", menu.getId());
            queue.addAll(childNodes);
            int parentIndex = result.indexOf(menu);
            result.addAll(parentIndex+1, childNodes);
            IDs.addAll(childNodes);
        }

        return result;
    }


//    public Map<Integer, List<Menu>> getAll() {
//
//        Map<Integer, List<Menu>> menuMap = new HashMap<Integer, List<Menu>>();
//
//        Iterator<Menu> iter = menuRepository.findAll().iterator();
//
//        while (iter.hasNext()) {
//            Menu menu = iter.next();
//            if(menuMap.containsKey(menu.getBelongTo())) menuMap.get(menu.getBelongTo()).add(menu);
//            else {
//                List<Menu> list = new ArrayList<Menu>();
//                list.add(menu);
//                menuMap.put(menu.getBelongTo(), list);
//            }
//        }
//
//        return menuMap;
//    }

}
