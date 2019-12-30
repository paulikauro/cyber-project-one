package sec.cyberprojectone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import sec.cyberprojectone.db.Database;
import sec.cyberprojectone.db.StringSerializer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@Controller
public class AccountController {
    @Autowired
    private Database db;

    @GetMapping("/register")
    public String getRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String postRegister(Account account) throws SQLException {
        db.persist(account);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String postLogin(
            Account account,
            Model model,
            HttpServletResponse res
            ) throws SQLException {
        Account real;
        try {
            real = db.stream(Account.class)
                    .filter(acc -> acc.getUsername().equals(account.getUsername()))
                    .findFirst().orElseThrow(LoginFailedException::new);
            if (!real.getPassword().equals(account.getPassword())) {
                throw new LoginFailedException();
            }
        } catch (LoginFailedException e) {
            // for debug
            e.printStackTrace();
            return loginFailed(model);
        }
        res.addCookie(new Cookie("sess", StringSerializer.serialized(real)));
        return "redirect:/notes/" + account.getUsername();
    }

    private String loginFailed(Model model) {
        model.addAttribute("fail", true);
        return "login";
    }

}
