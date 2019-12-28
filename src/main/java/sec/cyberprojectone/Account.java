package sec.cyberprojectone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sec.cyberprojectone.db.Entity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account extends Entity {
    // these are public for a reason; this is just a data holder
    public String username;
    public String password;

    {
        property(this::getUsername, this::setUsername);
        property(this::getPassword, this::setPassword);
    }
}
