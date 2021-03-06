package com.itheima.mall.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.mall.common.Result;
import com.itheima.mall.pojo.Member;
import com.itheima.mall.service.MemberService;
import com.itheima.mall.service.impl.MemberServiceImpl;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;

@WebServlet("/member/*")
public class MemberServlet extends BaseServlet {
    MemberService memberService = new MemberServiceImpl();  //调用业务层的方法
    public void login(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException{
        // response.setContentType("application/json");        //表示响应到前端的数据是一个json数据   "text/html"
        PrintWriter out = response.getWriter();

        //1、接收用户传过来的手机号和密码
        String mobile = request.getParameter("mobile");
        String pwd = request.getParameter("pwd");

        //2、调用业务层判断手机号和密码是否正确
        Member member = memberService.login(mobile, pwd);
        //对member对象进行判断，如果为null则表示登陆失败，如果不null则表示登陆成功

        //创建一个结果对象
        Result result = new Result();
        if(member==null){

            result.setFlag(false);
            result.setMsg("登录失败");
        }else{

            //登陆成功之后需要将用户的信息保存到session中。
            HttpSession session = request.getSession();
            session.setAttribute("member",member);

            result.setFlag(true);
            result.setMsg("登录成功");
        }
        //将结果对象转换成json
        wirteJson(response,result);
    }


    public void logout(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException{
        //销毁session
        HttpSession session = request.getSession();

        session.invalidate();

        Result result = new Result();
        result.setFlag(true);

        wirteJson(response,result);
    }



    public void register(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException{
        //如果前台数据很多的时候，可以使用BeanUtils.Populate()方法将表单的数据封装到实体对象中去
        //前提是要保存的的表单数据的名字要和实体对象属性名一致
        Map<String, String[]> parameterMap = request.getParameterMap();
        Member member = new Member();

        try {
            BeanUtils.populate(member,parameterMap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //设置用户的注册时间
        member.setRegister_time(new Date());

        //调用业务层完成添加的任务
        boolean result = memberService.regist(member);
        //根据注册结果返回对应的信息
        Result json = new Result();
        if (result == true){
            json.setFlag(true);
            json.setMsg("登录成功");
        }
        else{
            json.setFlag(false);
            json.setMsg("登录失败");
        }
        //转换成json
        wirteJson(response,result);
    }


    public void findNickName(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException{
        HttpSession session = request.getSession();  //无则创建，有则获取
        Member member = (Member)session.getAttribute("member");   //如果已经登陆过member对象不为null,否则为null

        //返回的结果对象
        Result result = new Result();
        result.setFlag(true);
        result.setMsg("");
        result.setData(member);

        //转换成json
        wirteJson(response,result);
    }

}
