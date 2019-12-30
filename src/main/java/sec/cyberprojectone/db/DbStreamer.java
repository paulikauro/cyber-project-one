package sec.cyberprojectone.db;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class DbStreamer {
    private final Connection conn;
    private final ResultSet rs;

    DbStreamer(Connection c, ResultSet r) {
        conn = c;
        rs = r;
    }

    <T> Stream<T> stream(Function<ResultSet, T> converter) {
        Spliterator<T> spliterator = new Spliterators.AbstractSpliterator<T>(
                Long.MAX_VALUE, Spliterator.ORDERED
        ) {
            @Override
            @SneakyThrows
            public boolean tryAdvance(Consumer<? super T> consumer) {
                if (!rs.next()) {
                    return false;
                }
                consumer.accept(converter.apply(rs));
                return true;
            }
        };
        return StreamSupport.stream(spliterator, false)
                .onClose(this::close);
    }

    @SneakyThrows
    void close() {
        conn.close();
    }
}
