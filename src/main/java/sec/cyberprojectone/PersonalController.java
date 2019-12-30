package sec.cyberprojectone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sec.cyberprojectone.db.Database;

import java.sql.SQLException;

import static sec.cyberprojectone.db.StringSerializer.deserializedInto;

@Controller
public class PersonalController {
    @Autowired
    private Database db;

    // refresh from database
    private Account refresh(Account acc) {
        return db.stream(Account.class)
                .filter(a -> a.getUsername().equals(acc.getUsername()))
                .findFirst().get();
    }

    // deserialize from session and refresh notes, etc. from database
    private Account getAcc(String sess) {
        Account sessAcc = new Account();
        deserializedInto(sessAcc, sess);
        return refresh(sessAcc);
    }

    @GetMapping("/notes/{user}")
    public String notes(
            Model model,
            @PathVariable String user,
            @CookieValue String sess
    ) {
        Account loggedIn = getAcc(sess);
        Account acc = db.stream(Account.class)
                .filter(a -> a.getUsername().equals(user))
                .findFirst().get();
        model.addAttribute("account", acc);
        return "notes";
    }

    @GetMapping("/personal")
    public String personal(Model model, @CookieValue String sess) {
        // refresh notes
        Account acc = getAcc(sess);
        model.addAttribute("account", acc);
        return "personal";
    }

    @PostMapping("/give_access")
    public String giveAccess(
            @RequestParam String username,
            @CookieValue String sess
    ) throws SQLException {
        Account acc = getAcc(sess);
        Account getsAccess = refresh(new Account(username, null, null, null, null));
        acc.getSharedWith().add(username);
        getsAccess.getHasAccessTo().add(acc.getUsername());
        db.persist(acc);
        db.persist(getsAccess);
        return "redirect:/personal";
    }

    @PostMapping("/post_note")
    public String postNote(
            @RequestParam String note,
            @CookieValue String sess
    ) throws SQLException {
        Account acc = getAcc(sess);
        acc.getNotes().add(note);
        db.persist(acc);
        return "redirect:/notes/" + acc.getUsername();
    }
}
