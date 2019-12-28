package sec.cyberprojectone.db;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class Property<T> {
    private final String columnName;
    private final Getter<T> getter;
    private final Setter<T> setter;
    private final String dataType;
}
