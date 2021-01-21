package com.htl.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.htl.entity.User;
import com.htl.enums.GenderEnum;
import com.htl.service.CartService;
import com.htl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * @author 韩天乐
 * @since 2020-12-08
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;

    /**
     * 注册
     */
    @PostMapping("/register")
    public String register(User user, Model model){
        boolean result = false;
        try {
            if(user.getGenderCode() == 1){
                user.setGender(GenderEnum.MAN);
            }
            if(user.getGenderCode() == 0){
                user.setGender(GenderEnum.WOMAN);
            }
            result = userService.save(user);
        } catch (Exception e) {
            model.addAttribute("error",user.getLoginName()+"已存在！请重新输入！");
            return "register";
        }
        if(result) return "login";
        return "register";
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public String login(String loginName, String password, HttpSession session){
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("login_name",loginName);
        wrapper.eq("password",password);
        User user = userService.getOne(wrapper);
        if (user == null){
            return "login";
        }else {
            session.setAttribute("user",user);
            return "redirect:/productCategory/list";
        }
    }

    /**
     * 退出
     */
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "login";
    }

    /**
     * 用户信息
     */
    @GetMapping("/userInfo")
    public ModelAndView userInfo(HttpSession session){
        User user = (User) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("userInfo");
        modelAndView.addObject("cartList",cartService.findAllCartVOByUserId(user.getId()));
        return modelAndView;
    }

}
