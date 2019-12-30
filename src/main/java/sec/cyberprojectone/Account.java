package sec.cyberprojectone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sec.cyberprojectone.db.Entity;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account extends Entity {
    private String username;
    private String password;
    private List<String> notes = new ArrayList<>();
    private List<String> sharedWith = new ArrayList<>();
    private List<String> hasAccessTo = new ArrayList<>();
    // TODO

    {
        primaryProperty(this::getUsername, this::setUsername);
        property(this::getPassword, this::setPassword);
        list(this::getNotes, this::setNotes);
        list(this::getSharedWith, this::setSharedWith);
        list(this::getHasAccessTo, this::setHasAccessTo);
    }
}
