package sec.cyberprojectone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String postLogin(Account account, Model model) throws SQLException {
        try {
            db.loadAccount(account);
        } catch (SQLException | LoginFailedException e) {
            e.printStackTrace();
            return loginFailed(model);
        }
        return "redirect:/register";
    }

    private String loginFailed(Model model) {
        model.addAttribute("fail", true);
        return "login";
    }

}