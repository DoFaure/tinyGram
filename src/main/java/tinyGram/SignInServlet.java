package tinyGram;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//With @WebServlet annotation the webapp/WEB-INF/web.xml is no longer required.
@WebServlet(
   name = "Login",
   description = "LoginAPI: Login / Logout with UserService",
   urlPatterns = "/login"
)
public class SignInServlet extends HttpServlet {

 @Override
 public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
   UserService userService = UserServiceFactory.getUserService();

   String thisUrl = req.getRequestURI();

   resp.setContentType("text/html");

     resp.getWriter()
         .println(
             "<p>Please <a href=\"" + userService.createLoginURL("/../homepage.jsp") + "\">sign in</a>.</p>");
 }
}
//[END users_API_example]