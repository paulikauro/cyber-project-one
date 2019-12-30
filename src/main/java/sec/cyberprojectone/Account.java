package sec.cyberprojectone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sec.cyberprojectone.db.Entity;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account extends Entity {
    private String username;
    private String password;
    private ArrayList<String> notes = new ArrayList<>();
    private ArrayList<String> sharedWith = new ArrayList<>();
    private ArrayList<String> hasAccessTo = new ArrayList<>();
    // TODO

    {
        property(this::getUsername, this::setUsername);
        property(this::getPassword, this::setPassword);
    }
}
