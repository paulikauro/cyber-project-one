package sec.cyberprojectone.db;

@FunctionalInterface
public interface Setter<T> {
    void set(T value);
}
