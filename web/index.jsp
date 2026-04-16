<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
    } else {
        switch (user.getRole()) {
            case "admin":
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                break;
            case "ngo":
                response.sendRedirect(request.getContextPath() + "/ngo/dashboard");
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/donor/dashboard");
                break;
        }
    }
%>
