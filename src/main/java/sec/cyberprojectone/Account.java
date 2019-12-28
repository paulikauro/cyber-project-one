package sec.cyberprojectone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sec.cyberprojectone.db.Entity;
import sec.cyberprojectone.db.DbEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account implements DbEntity {
    // these are public for a reason; this is just a data holder
    public String username;
    public String password;

    public Entity properties() {
        // so hacky lol
        Account me = this;
        return new Entity() {{
            property(me::getUsername, me::setUsername);
            property(me::getPassword, me::setPassword);
        }};
    }
    @Override
    public String toString() {
        return properties().toString();
    }
}
