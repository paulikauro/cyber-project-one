package sec.cyberprojectone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    // these are public for a reason; this is just a data holder
    public String username;
    public String password;
}
